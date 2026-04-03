package com.bookstore.payment.interfaces.rest.dto;

import com.bookstore.payment.domain.port.in.GetPaymentUseCase;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, BigDecimal amount, String currency, String status) {

    public static PaymentResponse from(GetPaymentUseCase.PaymentView view) {
        return new PaymentResponse(view.id(), view.orderId(), view.amount(), view.currency(), view.status());
    }
}
