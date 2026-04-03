package com.bookstore.payment.infrastructure.persistence.adapter;

import com.bookstore.payment.domain.model.Payment;
import com.bookstore.payment.domain.model.PaymentStatus;
import com.bookstore.payment.domain.port.out.PaymentRepository;
import com.bookstore.payment.infrastructure.persistence.entity.PaymentJpaEntity;
import com.bookstore.payment.infrastructure.persistence.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        var entity = toEntity(payment);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }

    private PaymentJpaEntity toEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.id(),
                payment.orderId(),
                payment.amount(),
                payment.currency(),
                PaymentJpaEntity.PaymentStatusJpa.valueOf(payment.status().name())
        );
    }

    private Payment toDomain(PaymentJpaEntity entity) {
        return new Payment(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                entity.getCurrency(),
                PaymentStatus.valueOf(entity.getStatus().name())
        );
    }
}
