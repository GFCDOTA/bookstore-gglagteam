package com.bookstore.payment.application.usecase;

import com.bookstore.payment.domain.event.PaymentApprovedEvent;
import com.bookstore.payment.domain.event.PaymentRejectedEvent;
import com.bookstore.payment.domain.model.Payment;
import com.bookstore.payment.domain.port.in.ProcessPaymentUseCase;
import com.bookstore.payment.domain.port.out.EventPublisher;
import com.bookstore.payment.domain.port.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProcessPaymentUseCaseImpl implements ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    private final Random random = new Random();

    @Override
    public void execute(UUID orderId, BigDecimal amount) {
        // Idempotency check
        if (paymentRepository.existsByOrderId(orderId)) {
            log.warn("Payment already processed for orderId={}", orderId);
            return;
        }

        var payment = Payment.create(orderId, amount);

        // Simulate payment processing: 70% approval, 30% rejection
        boolean approved = random.nextInt(100) < 70;

        if (approved) {
            payment.approve();
            paymentRepository.save(payment);

            eventPublisher.publish(PaymentApprovedEvent.of(orderId, payment.id(), amount));
            log.info("Payment approved orderId={} paymentId={} amount={}", orderId, payment.id(), amount);
        } else {
            payment.reject();
            paymentRepository.save(payment);

            eventPublisher.publish(PaymentRejectedEvent.of(orderId, payment.id(), "Payment declined by processor"));
            log.info("Payment rejected orderId={} paymentId={}", orderId, payment.id());
        }
    }
}
