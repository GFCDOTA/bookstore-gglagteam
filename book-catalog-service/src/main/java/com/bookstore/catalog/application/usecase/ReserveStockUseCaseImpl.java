package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.event.StockReservationFailedEvent;
import com.bookstore.catalog.domain.event.StockReservedEvent;
import com.bookstore.catalog.domain.port.in.ReserveStockUseCase;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.port.out.EventPublisher;
import com.bookstore.catalog.domain.vo.BookId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReserveStockUseCaseImpl implements ReserveStockUseCase {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    @Override
    public void execute(UUID orderId, List<StockReservationItem> items) {
        log.info("Reserving stock for orderId={} items={}", orderId, items.size());

        for (var item : items) {
            try {
                var book = bookRepository.findById(BookId.of(item.bookId()))
                        .orElseThrow(() -> new IllegalStateException("Book not found: " + item.bookId()));

                book.reserveStock(item.quantity());
                bookRepository.save(book);

                eventPublisher.publish(StockReservedEvent.of(orderId, item.bookId(), item.quantity()));

                log.info("Stock reserved orderId={} bookId={} quantity={}", orderId, item.bookId(), item.quantity());
            } catch (Exception e) {
                log.error("Stock reservation failed orderId={} bookId={} reason={}",
                        orderId, item.bookId(), e.getMessage());
                eventPublisher.publish(StockReservationFailedEvent.of(orderId, e.getMessage()));
                return;
            }
        }
    }
}
