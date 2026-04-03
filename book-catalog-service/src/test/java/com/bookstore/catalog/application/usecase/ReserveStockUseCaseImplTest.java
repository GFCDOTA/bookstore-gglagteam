package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.event.StockReservationFailedEvent;
import com.bookstore.catalog.domain.event.StockReservedEvent;
import com.bookstore.catalog.domain.model.Book;
import com.bookstore.catalog.domain.model.BookStatus;
import com.bookstore.catalog.domain.port.in.ReserveStockUseCase.StockReservationItem;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.port.out.EventPublisher;
import com.bookstore.catalog.domain.vo.BookId;
import com.bookstore.catalog.domain.vo.ISBN;
import com.bookstore.catalog.domain.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveStockUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private EventPublisher eventPublisher;

    private ReserveStockUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReserveStockUseCaseImpl(bookRepository, eventPublisher);
    }

    private Book createBook(UUID id, int stock) {
        return new Book(BookId.of(id), "Title", "Author",
                new ISBN("9780134494166"), Money.of(50.0), stock, BookStatus.ACTIVE);
    }

    @Test
    @DisplayName("should reserve stock and publish StockReserved event")
    void shouldReserveStockSuccessfully() {
        var bookId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var book = createBook(bookId, 10);

        when(bookRepository.findById(BookId.of(bookId))).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(orderId, List.of(new StockReservationItem(bookId, 3)));

        verify(bookRepository).save(book);
        verify(eventPublisher).publish(any(StockReservedEvent.class));
    }

    @Test
    @DisplayName("should publish StockReservationFailed when book not found")
    void shouldPublishFailedWhenBookNotFound() {
        var bookId = UUID.randomUUID();
        var orderId = UUID.randomUUID();

        when(bookRepository.findById(any())).thenReturn(Optional.empty());

        useCase.execute(orderId, List.of(new StockReservationItem(bookId, 3)));

        verify(bookRepository, never()).save(any());
        verify(eventPublisher).publish(any(StockReservationFailedEvent.class));
    }

    @Test
    @DisplayName("should publish StockReservationFailed when insufficient stock")
    void shouldPublishFailedWhenInsufficientStock() {
        var bookId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var book = createBook(bookId, 2);

        when(bookRepository.findById(BookId.of(bookId))).thenReturn(Optional.of(book));

        useCase.execute(orderId, List.of(new StockReservationItem(bookId, 5)));

        verify(eventPublisher).publish(any(StockReservationFailedEvent.class));
    }

    @Test
    @DisplayName("should reserve stock for multiple items")
    void shouldReserveStockForMultipleItems() {
        var bookId1 = UUID.randomUUID();
        var bookId2 = UUID.randomUUID();
        var orderId = UUID.randomUUID();

        when(bookRepository.findById(BookId.of(bookId1))).thenReturn(Optional.of(createBook(bookId1, 10)));
        when(bookRepository.findById(BookId.of(bookId2))).thenReturn(Optional.of(createBook(bookId2, 20)));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(orderId, List.of(
                new StockReservationItem(bookId1, 2),
                new StockReservationItem(bookId2, 5)
        ));

        verify(bookRepository, times(2)).save(any());
        verify(eventPublisher, times(2)).publish(any(StockReservedEvent.class));
    }
}
