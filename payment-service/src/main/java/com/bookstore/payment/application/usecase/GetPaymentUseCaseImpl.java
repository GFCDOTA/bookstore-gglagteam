package com.bookstore.payment.application.usecase;

import com.bookstore.payment.domain.port.in.GetPaymentUseCase;
import com.bookstore.payment.domain.port.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPaymentUseCaseImpl implements GetPaymentUseCase {

    private final PaymentRepository paymentRepository;

    @Override
    public Optional<PaymentView> execute(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> new PaymentView(
                        payment.id(),
                        payment.orderId(),
                        payment.amount(),
                        payment.currency(),
                        payment.status().name()
                ));
    }
}
