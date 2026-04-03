package com.bookstore.payment.interfaces.rest.controller;

import com.bookstore.payment.domain.port.in.GetPaymentUseCase;
import com.bookstore.payment.interfaces.exception.PaymentNotFoundException;
import com.bookstore.payment.interfaces.rest.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final GetPaymentUseCase getPaymentUseCase;

    @GetMapping("/{orderId}")
    public PaymentResponse findByOrderId(@PathVariable UUID orderId) {
        return getPaymentUseCase.execute(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
    }
}
