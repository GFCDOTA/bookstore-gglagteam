package com.bookstore.catalog.infrastructure.persistence.repository;

import com.bookstore.catalog.infrastructure.persistence.entity.BookJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookJpaRepository extends JpaRepository<BookJpaEntity, UUID> {
    boolean existsByIsbn(String isbn);
}
