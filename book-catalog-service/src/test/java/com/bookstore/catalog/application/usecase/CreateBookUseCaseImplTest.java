package com.bookstore.catalog.application.usecase;

import com.bookstore.catalog.domain.model.Book;
import com.bookstore.catalog.domain.model.BookStatus;
import com.bookstore.catalog.domain.port.in.CreateBookUseCase.CreateBookCommand;
import com.bookstore.catalog.domain.port.out.BookRepository;
import com.bookstore.catalog.domain.port.out.EventPublisher;
import com.bookstore.catalog.domain.vo.ISBN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookUseCaseImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private EventPublisher eventPublisher;

    private CreateBookUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBookUseCaseImpl(bookRepository, eventPublisher);
    }

    @Test
    @DisplayName("should create book and publish event")
    void shouldCreateBookAndPublishEvent() {
        var command = new CreateBookCommand("Clean Architecture", "Robert C. Martin",
                "9780134494166", new BigDecimal("89.90"), 50);

        when(bookRepository.existsByIsbn(any(ISBN.class))).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertNotNull(result.bookId());
        assertEquals("Clean Architecture", result.title());
        assertEquals(BookStatus.ACTIVE.name(), result.status());

        verify(bookRepository).save(any(Book.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    @DisplayName("should throw when ISBN already exists")
    void shouldThrowWhenIsbnAlreadyExists() {
        var command = new CreateBookCommand("Title", "Author",
                "9780134494166", new BigDecimal("89.90"), 10);

        when(bookRepository.existsByIsbn(any(ISBN.class))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

        verify(bookRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("should save book with correct attributes")
    void shouldSaveBookWithCorrectAttributes() {
        var command = new CreateBookCommand("DDD", "Eric Evans",
                "9780321125217", new BigDecimal("120.00"), 30);

        when(bookRepository.existsByIsbn(any())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        var bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());

        var savedBook = bookCaptor.getValue();
        assertEquals("DDD", savedBook.title());
        assertEquals("Eric Evans", savedBook.author());
        assertEquals(30, savedBook.stockQuantity());
    }
}
