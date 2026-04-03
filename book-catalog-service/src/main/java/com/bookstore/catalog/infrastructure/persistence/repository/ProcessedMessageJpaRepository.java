package com.bookstore.catalog.infrastructure.persistence.repository;

import com.bookstore.catalog.infrastructure.persistence.entity.ProcessedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageJpaRepository extends JpaRepository<ProcessedMessageEntity, String> {
}
