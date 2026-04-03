package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.event.BookCreatedEvent;
import com.bookstore.catalog.domain.model.Book;
import com.bookstore.catalog.domain.port.in.CreateBookUseCase;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.port.out.EventPublisher;
import com.bookstore.catalog.domain.vo.ISBN;
import com.bookstore.catalog.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CreateBookUseCaseImpl implements CreateBookUseCase {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    @Override
    public CreateBookResult execute(CreateBookCommand command) {
        var isbn = new ISBN(command.isbn());

        if (bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Book with ISBN " + command.isbn() + " already exists");
        }

        var book = Book.create(
                command.title(),
                command.author(),
                isbn,
                Money.of(command.price()),
                command.initialStock()
        );

        var savedBook = bookRepository.save(book);

        var event = BookCreatedEvent.of(
                savedBook.id().value(),
                savedBook.title(),
                savedBook.author(),
                savedBook.isbn().value(),
                savedBook.price().amount(),
                savedBook.stockQuantity()
        );
        eventPublisher.publish(event);

        log.info("Book created bookId={} title={} isbn={}", savedBook.id(), savedBook.title(), savedBook.isbn());

        return new CreateBookResult(savedBook.id().value(), savedBook.title(), savedBook.status().name());
    }
}
