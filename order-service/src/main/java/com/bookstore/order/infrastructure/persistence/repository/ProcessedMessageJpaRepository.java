package com.bookstore.order.infrastructure.persistence.repository;

import com.bookstore.order.infrastructure.persistence.entity.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageJpaRepository extends JpaRepository<ProcessedMessageEntity, String> {
}
