package com.bookstore.catalog.infrastructure.persistence.adapter;

import com.bookstore.catalog.domain.model.Book;
import com.bookstore.catalog.domain.model.BookStatus;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.vo.BookId;
import com.bookstore.catalog.domain.vo.ISBN;
import com.bookstore.catalog.domain.vo.Money;
import com.bookstore.catalog.infrastructure.persistence.entity.BookJpaEntity;
import com.bookstore.catalog.infrastructure.persistence.repository.BookJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookRepository {

    private final BookJpaRepository jpaRepository;

    @Override
    public Book save(Book book) {
        var entity = toEntity(book);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByIsbn(ISBN isbn) {
        return jpaRepository.existsByIsbn(isbn.value());
    }

    private BookJpaEntity toEntity(Book book) {
        return new BookJpaEntity(
                book.id().value(),
                book.title(),
                book.author(),
                book.isbn().value(),
                book.price().amount(),
                book.price().currency().getCurrencyCode(),
                book.stockQuantity(),
                BookJpaEntity.BookStatusJpa.valueOf(book.status().name())
        );
    }

    private Book toDomain(BookJpaEntity entity) {
        return new Book(
                BookId.of(entity.getId()),
                entity.getTitle(),
                entity.getAuthor(),
                new ISBN(entity.getIsbn()),
                new Money(entity.getPrice(), Currency.getInstance(entity.getPriceCurrency())),
                entity.getStockQuantity(),
                BookStatus.valueOf(entity.getStatus().name())
        );
    }
}
