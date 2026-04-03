package com.bookstore.payment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create payment with PENDING status")
        void shouldCreateWithPendingStatus() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));

            assertEquals(PaymentStatus.PENDING, payment.status());
            assertNotNull(payment.id());
        }

        @Test
        @DisplayName("should create payment with BRL currency")
        void shouldCreateWithBrlCurrency() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));

            assertEquals("BRL", payment.currency());
        }

        @Test
        @DisplayName("should preserve orderId and amount")
        void shouldPreserveOrderIdAndAmount() {
            var orderId = UUID.randomUUID();
            var amount = new BigDecimal("250.00");

            var payment = Payment.create(orderId, amount);

            assertEquals(orderId, payment.orderId());
            assertEquals(amount, payment.amount());
        }
    }

    @Nested
    @DisplayName("approve()")
    class Approve {

        @Test
        @DisplayName("should transition from PENDING to APPROVED")
        void shouldTransitionToApproved() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));

            payment.approve();

            assertEquals(PaymentStatus.APPROVED, payment.status());
        }

        @Test
        @DisplayName("should throw when already approved")
        void shouldThrowWhenAlreadyApproved() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));
            payment.approve();

            assertThrows(IllegalStateException.class, payment::approve);
        }

        @Test
        @DisplayName("should throw when already rejected")
        void shouldThrowWhenAlreadyRejected() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));
            payment.reject();

            assertThrows(IllegalStateException.class, payment::approve);
        }
    }

    @Nested
    @DisplayName("reject()")
    class Reject {

        @Test
        @DisplayName("should transition from PENDING to REJECTED")
        void shouldTransitionToRejected() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));

            payment.reject();

            assertEquals(PaymentStatus.REJECTED, payment.status());
        }

        @Test
        @DisplayName("should throw when already approved")
        void shouldThrowWhenAlreadyApproved() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));
            payment.approve();

            assertThrows(IllegalStateException.class, payment::reject);
        }

        @Test
        @DisplayName("should throw when already rejected")
        void shouldThrowWhenAlreadyRejected() {
            var payment = Payment.create(UUID.randomUUID(), new BigDecimal("100.00"));
            payment.reject();

            assertThrows(IllegalStateException.class, payment::reject);
        }
    }
}
