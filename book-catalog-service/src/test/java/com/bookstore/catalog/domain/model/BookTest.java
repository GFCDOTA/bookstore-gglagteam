package com.bookstore.catalog.domain.model;

import com.bookstore.catalog.domain.vo.ISBN;
import com.bookstore.catalog.domain.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private static final ISBN VALID_ISBN = new ISBN("9780134494166");
    private static final Money VALID_PRICE = Money.of(89.90);

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create book with valid parameters")
        void shouldCreateBookWithValidParameters() {
            var book = Book.create("Clean Architecture", "Robert C. Martin", VALID_ISBN, VALID_PRICE, 50);

            assertNotNull(book.id());
            assertEquals("Clean Architecture", book.title());
            assertEquals("Robert C. Martin", book.author());
            assertEquals(VALID_ISBN, book.isbn());
            assertEquals(VALID_PRICE, book.price());
            assertEquals(50, book.stockQuantity());
            assertEquals(BookStatus.ACTIVE, book.status());
        }

        @Test
        @DisplayName("should throw when title is null")
        void shouldThrowWhenTitleIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Book.create(null, "Author", VALID_ISBN, VALID_PRICE, 10));
        }

        @Test
        @DisplayName("should throw when title has less than 2 characters")
        void shouldThrowWhenTitleTooShort() {
            assertThrows(IllegalArgumentException.class,
                    () -> Book.create("A", "Author", VALID_ISBN, VALID_PRICE, 10));
        }

        @Test
        @DisplayName("should throw when author is blank")
        void shouldThrowWhenAuthorIsBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> Book.create("Title", "  ", VALID_ISBN, VALID_PRICE, 10));
        }

        @Test
        @DisplayName("should throw when price is negative")
        void shouldThrowWhenPriceIsNegative() {
            assertThrows(IllegalArgumentException.class,
                    () -> Book.create("Title", "Author", VALID_ISBN, Money.of(-1.0), 10));
        }

        @Test
        @DisplayName("should throw when initial stock is negative")
        void shouldThrowWhenInitialStockIsNegative() {
            assertThrows(IllegalArgumentException.class,
                    () -> Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, -1));
        }

        @Test
        @DisplayName("should allow zero initial stock")
        void shouldAllowZeroInitialStock() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 0);
            assertEquals(0, book.stockQuantity());
        }
    }

    @Nested
    @DisplayName("updatePrice()")
    class UpdatePrice {

        @Test
        @DisplayName("should update price to new value")
        void shouldUpdatePrice() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);
            var newPrice = Money.of(120.00);

            book.updatePrice(newPrice);

            assertEquals(newPrice, book.price());
        }

        @Test
        @DisplayName("should throw when new price is negative")
        void shouldThrowWhenNewPriceIsNegative() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            assertThrows(IllegalArgumentException.class,
                    () -> book.updatePrice(Money.of(-5.0)));
        }
    }

    @Nested
    @DisplayName("updateStock()")
    class UpdateStock {

        @Test
        @DisplayName("should update stock quantity")
        void shouldUpdateStock() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            book.updateStock(25);

            assertEquals(25, book.stockQuantity());
        }

        @Test
        @DisplayName("should throw when new quantity is negative")
        void shouldThrowWhenQuantityIsNegative() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            assertThrows(IllegalArgumentException.class, () -> book.updateStock(-1));
        }
    }

    @Nested
    @DisplayName("reserveStock()")
    class ReserveStock {

        @Test
        @DisplayName("should reserve stock successfully")
        void shouldReserveStock() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            book.reserveStock(3);

            assertEquals(7, book.stockQuantity());
        }

        @Test
        @DisplayName("should reserve all available stock")
        void shouldReserveAllStock() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 5);

            book.reserveStock(5);

            assertEquals(0, book.stockQuantity());
        }

        @Test
        @DisplayName("should throw when insufficient stock")
        void shouldThrowWhenInsufficientStock() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 5);

            assertThrows(IllegalStateException.class, () -> book.reserveStock(6));
        }

        @Test
        @DisplayName("should throw when quantity is zero")
        void shouldThrowWhenQuantityIsZero() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            assertThrows(IllegalArgumentException.class, () -> book.reserveStock(0));
        }

        @Test
        @DisplayName("should throw when quantity is negative")
        void shouldThrowWhenQuantityIsNegative() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            assertThrows(IllegalArgumentException.class, () -> book.reserveStock(-1));
        }
    }

    @Nested
    @DisplayName("releaseStock()")
    class ReleaseStock {

        @Test
        @DisplayName("should release stock successfully")
        void shouldReleaseStock() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);
            book.reserveStock(3);

            book.releaseStock(3);

            assertEquals(10, book.stockQuantity());
        }

        @Test
        @DisplayName("should throw when release quantity is zero")
        void shouldThrowWhenReleaseQuantityIsZero() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            assertThrows(IllegalArgumentException.class, () -> book.releaseStock(0));
        }
    }

    @Nested
    @DisplayName("status transitions")
    class StatusTransitions {

        @Test
        @DisplayName("should deactivate book")
        void shouldDeactivateBook() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);

            book.deactivate();

            assertEquals(BookStatus.INACTIVE, book.status());
        }

        @Test
        @DisplayName("should activate book")
        void shouldActivateBook() {
            var book = Book.create("Title", "Author", VALID_ISBN, VALID_PRICE, 10);
            book.deactivate();

            book.activate();

            assertEquals(BookStatus.ACTIVE, book.status());
        }
    }
}
