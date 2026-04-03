package com.bookstore.order.domain.model;

import com.bookstore.order.domain.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("should create OrderItem with valid parameters")
    void shouldCreateWithValidParameters() {
        var bookId = UUID.randomUUID();
        var item = new OrderItem(bookId, 3, Money.of(29.90));

        assertEquals(bookId, item.bookId());
        assertEquals(3, item.quantity());
        assertEquals(Money.of(29.90), item.unitPrice());
    }

    @Test
    @DisplayName("should throw when bookId is null")
    void shouldThrowWhenBookIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(null, 1, Money.of(10.0)));
    }

    @Test
    @DisplayName("should throw when quantity is zero")
    void shouldThrowWhenQuantityIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(UUID.randomUUID(), 0, Money.of(10.0)));
    }

    @Test
    @DisplayName("should throw when quantity is negative")
    void shouldThrowWhenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(UUID.randomUUID(), -1, Money.of(10.0)));
    }

    @Test
    @DisplayName("should throw when unitPrice is null")
    void shouldThrowWhenUnitPriceIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(UUID.randomUUID(), 1, null));
    }
}
