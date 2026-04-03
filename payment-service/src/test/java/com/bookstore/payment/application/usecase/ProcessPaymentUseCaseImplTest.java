package com.bookstore.payment.application.usecase;

import com.bookstore.payment.domain.event.DomainEvent;
import com.bookstore.payment.domain.event.PaymentApprovedEvent;
import com.bookstore.payment.domain.event.PaymentRejectedEvent;
import com.bookstore.payment.domain.model.Payment;
import com.bookstore.payment.domain.model.PaymentStatus;
import com.bookstore.payment.domain.port.out.EventPublisher;
import com.bookstore.payment.domain.port.out.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUseCaseImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EventPublisher eventPublisher;

    private ProcessPaymentUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcessPaymentUseCaseImpl(paymentRepository, eventPublisher);
    }

    @Test
    @DisplayName("should skip processing when payment already exists for orderId")
    void shouldSkipWhenPaymentAlreadyExists() {
        var orderId = UUID.randomUUID();
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(true);

        useCase.execute(orderId, new BigDecimal("100.00"));

        verify(paymentRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("should save payment and publish event for new orderId")
    void shouldSavePaymentAndPublishEvent() {
        var orderId = UUID.randomUUID();
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(orderId, new BigDecimal("100.00"));

        verify(paymentRepository).save(any(Payment.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    @DisplayName("should save payment with correct status (approved or rejected)")
    void shouldSavePaymentWithCorrectStatus() {
        var orderId = UUID.randomUUID();
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(orderId, new BigDecimal("100.00"));

        var paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        var saved = paymentCaptor.getValue();
        assertTrue(saved.status() == PaymentStatus.APPROVED || saved.status() == PaymentStatus.REJECTED);
    }

    @Test
    @DisplayName("should publish PaymentApproved or PaymentRejected event")
    void shouldPublishCorrectEventType() {
        var orderId = UUID.randomUUID();
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(orderId, new BigDecimal("100.00"));

        var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        var event = eventCaptor.getValue();
        assertTrue(event instanceof PaymentApprovedEvent || event instanceof PaymentRejectedEvent,
                "Event should be PaymentApproved or PaymentRejected, got: " + event.getClass().getSimpleName());
    }

    @Test
    @DisplayName("should handle multiple executions with idempotency")
    void shouldHandleIdempotency() {
        var orderId = UUID.randomUUID();
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(orderId, new BigDecimal("100.00"));
        useCase.execute(orderId, new BigDecimal("100.00"));

        verify(paymentRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publish(any());
    }
}
