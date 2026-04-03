package com.bookstore.order.domain.model;

import com.bookstore.order.domain.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private OrderItem createItem(double price, int qty) {
        return new OrderItem(UUID.randomUUID(), qty, Money.of(price));
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create order with AWAITING_STOCK status")
        void shouldCreateWithAwaitingStockStatus() {
            var items = List.of(createItem(50.0, 2));
            var order = Order.create(UUID.randomUUID(), items);

            assertEquals(OrderStatus.AWAITING_STOCK, order.status());
            assertNotNull(order.id());
        }

        @Test
        @DisplayName("should calculate total amount from items")
        void shouldCalculateTotalAmount() {
            var items = List.of(
                    createItem(50.0, 2),
                    createItem(30.0, 3)
            );
            var order = Order.create(UUID.randomUUID(), items);

            assertEquals(new BigDecimal("190.00"), order.totalAmount().amount());
        }

        @Test
        @DisplayName("should preserve customer ID")
        void shouldPreserveCustomerId() {
            var customerId = UUID.randomUUID();
            var order = Order.create(customerId, List.of(createItem(10.0, 1)));

            assertEquals(customerId, order.customerId());
        }

        @Test
        @DisplayName("should return unmodifiable items list")
        void shouldReturnUnmodifiableItems() {
            var items = List.of(createItem(10.0, 1));
            var order = Order.create(UUID.randomUUID(), items);

            assertThrows(UnsupportedOperationException.class,
                    () -> order.items().add(createItem(20.0, 1)));
        }
    }

    @Nested
    @DisplayName("markAwaitingPayment()")
    class MarkAwaitingPayment {

        @Test
        @DisplayName("should transition to AWAITING_PAYMENT")
        void shouldTransitionToAwaitingPayment() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));

            order.markAwaitingPayment();

            assertEquals(OrderStatus.AWAITING_PAYMENT, order.status());
        }

        @Test
        @DisplayName("should throw when order is already confirmed")
        void shouldThrowWhenAlreadyConfirmed() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));
            order.confirm();

            assertThrows(IllegalStateException.class, order::markAwaitingPayment);
        }

        @Test
        @DisplayName("should throw when order is already cancelled")
        void shouldThrowWhenAlreadyCancelled() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));
            order.cancel("test");

            assertThrows(IllegalStateException.class, order::markAwaitingPayment);
        }
    }

    @Nested
    @DisplayName("confirm()")
    class Confirm {

        @Test
        @DisplayName("should transition to CONFIRMED")
        void shouldTransitionToConfirmed() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));
            order.markAwaitingPayment();

            order.confirm();

            assertEquals(OrderStatus.CONFIRMED, order.status());
        }

        @Test
        @DisplayName("should throw when order is already cancelled")
        void shouldThrowWhenAlreadyCancelled() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));
            order.cancel("test");

            assertThrows(IllegalStateException.class, order::confirm);
        }
    }

    @Nested
    @DisplayName("cancel()")
    class Cancel {

        @Test
        @DisplayName("should transition to CANCELLED")
        void shouldTransitionToCancelled() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));

            order.cancel("out of stock");

            assertEquals(OrderStatus.CANCELLED, order.status());
        }

        @Test
        @DisplayName("should throw when order is already confirmed")
        void shouldThrowWhenAlreadyConfirmed() {
            var order = Order.create(UUID.randomUUID(), List.of(createItem(10.0, 1)));
            order.confirm();

            assertThrows(IllegalStateException.class, () -> order.cancel("test"));
        }
    }
}
