package com.bookstore.payment.infrastructure.persistence.repository;

import com.bookstore.payment.infrastructure.persistence.entity.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageJpaRepository extends JpaRepository<ProcessedMessageEntity, String> {
}
