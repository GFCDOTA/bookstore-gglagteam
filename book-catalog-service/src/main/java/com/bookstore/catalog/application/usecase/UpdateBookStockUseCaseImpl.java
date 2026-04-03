package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.event.StockUpdatedEvent;
import com.bookstore.catalog.domain.port.in.UpdateBookStockUseCase;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.port.out.EventPublisher;
import com.bookstore.catalog.domain.vo.BookId;
import com.bookstore.catalog.interfaces.exception.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UpdateBookStockUseCaseImpl implements UpdateBookStockUseCase {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    @Override
    public void execute(UUID bookId, int newQuantity) {
        var book = bookRepository.findById(BookId.of(bookId))
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

        book.updateStock(newQuantity);
        bookRepository.save(book);

        eventPublisher.publish(StockUpdatedEvent.of(bookId, newQuantity));

        log.info("Book stock updated bookId={} newStock={}", bookId, newQuantity);
    }
}
