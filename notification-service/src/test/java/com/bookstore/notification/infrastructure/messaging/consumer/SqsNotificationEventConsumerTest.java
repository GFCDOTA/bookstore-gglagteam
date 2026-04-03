package com.bookstore.notification.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import static org.junit.jupiter.api.Assertions.*;

class SqsNotificationEventConsumerTest {

    private SqsNotificationEventConsumer consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        consumer = new SqsNotificationEventConsumer(objectMapper);
    }

    @Test
    @DisplayName("should process OrderConfirmed event without errors")
    void shouldProcessOrderConfirmedEvent() {
        var json = """
                {
                    "eventType": "OrderConfirmed",
                    "orderId": "123e4567-e89b-12d3-a456-426614174000",
                    "customerId": "550e8400-e29b-41d4-a716-446655440000"
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(new GenericMessage<>(json)));
    }

    @Test
    @DisplayName("should process OrderCancelled event without errors")
    void shouldProcessOrderCancelledEvent() {
        var json = """
                {
                    "eventType": "OrderCancelled",
                    "orderId": "123e4567-e89b-12d3-a456-426614174000",
                    "customerId": "550e8400-e29b-41d4-a716-446655440000",
                    "reason": "Payment rejected"
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(new GenericMessage<>(json)));
    }

    @Test
    @DisplayName("should process PaymentApproved event without errors")
    void shouldProcessPaymentApprovedEvent() {
        var json = """
                {
                    "eventType": "PaymentApproved",
                    "orderId": "123e4567-e89b-12d3-a456-426614174000",
                    "paymentId": "987fcdeb-51a2-4c3b-8e7f-123456789abc",
                    "amount": "89.90"
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(new GenericMessage<>(json)));
    }

    @Test
    @DisplayName("should process PaymentRejected event without errors")
    void shouldProcessPaymentRejectedEvent() {
        var json = """
                {
                    "eventType": "PaymentRejected",
                    "orderId": "123e4567-e89b-12d3-a456-426614174000",
                    "reason": "Insufficient funds"
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(new GenericMessage<>(json)));
    }

    @Test
    @DisplayName("should handle unknown event type gracefully")
    void shouldHandleUnknownEventType() {
        var json = """
                {
                    "eventType": "UnknownEvent",
                    "data": "something"
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(new GenericMessage<>(json)));
    }

    @Test
    @DisplayName("should handle missing fields gracefully")
    void shouldHandleMissingFieldsGracefully() {
        var json = """
                {
                    "eventType": "OrderConfirmed"
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(new GenericMessage<>(json)));
    }

    @Test
    @DisplayName("should throw for invalid JSON")
    void shouldThrowForInvalidJson() {
        assertThrows(RuntimeException.class,
                () -> consumer.consume(new GenericMessage<>("not valid json")));
    }
}
