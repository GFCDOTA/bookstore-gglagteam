package com.bookstore.notification.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsNotificationEventConsumer {

    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.notification-queue-name}")
    public void consume(Message<String> message) {
        try {
            var node = objectMapper.readTree(message.getPayload());
            var eventType = node.has("eventType") ? node.get("eventType").asText() : "Unknown";

            switch (eventType) {
                case "OrderConfirmed" -> sendOrderConfirmedNotification(node);
                case "OrderCancelled" -> sendOrderCancelledNotification(node);
                case "PaymentApproved" -> sendPaymentApprovedNotification(node);
                case "PaymentRejected" -> sendPaymentRejectedNotification(node);
                default -> log.debug("Unhandled notification event type={}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed to process notification message", e);
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    private void sendOrderConfirmedNotification(JsonNode node) {
        var orderId = node.has("orderId") ? node.get("orderId").asText() : "unknown";
        var customerId = node.has("customerId") ? node.get("customerId").asText() : "unknown";

        log.info("=== EMAIL NOTIFICATION ===");
        log.info("To: customer-{}@bookstore.com", customerId);
        log.info("Subject: Order Confirmed - #{}", orderId);
        log.info("Body: Your order #{} has been confirmed and will be shipped soon.", orderId);
        log.info("=== END NOTIFICATION ===");
    }

    private void sendOrderCancelledNotification(JsonNode node) {
        var orderId = node.has("orderId") ? node.get("orderId").asText() : "unknown";
        var customerId = node.has("customerId") ? node.get("customerId").asText() : "unknown";

        log.info("=== EMAIL NOTIFICATION ===");
        log.info("To: customer-{}@bookstore.com", customerId);
        log.info("Subject: Order Cancelled - #{}", orderId);
        log.info("Body: Your order #{} has been cancelled. If you have any questions, contact support.", orderId);
        log.info("=== END NOTIFICATION ===");
    }

    private void sendPaymentApprovedNotification(JsonNode node) {
        var orderId = node.has("orderId") ? node.get("orderId").asText() : "unknown";
        var paymentId = node.has("paymentId") ? node.get("paymentId").asText() : "unknown";
        var amount = node.has("amount") ? node.get("amount").asText() : "0.00";

        log.info("=== EMAIL NOTIFICATION ===");
        log.info("Subject: Payment Approved for Order #{}", orderId);
        log.info("Body: Payment #{} of R$ {} for order #{} has been approved.", paymentId, amount, orderId);
        log.info("=== END NOTIFICATION ===");
    }

    private void sendPaymentRejectedNotification(JsonNode node) {
        var orderId = node.has("orderId") ? node.get("orderId").asText() : "unknown";
        var reason = node.has("reason") ? node.get("reason").asText() : "Unknown reason";

        log.info("=== EMAIL NOTIFICATION ===");
        log.info("Subject: Payment Rejected for Order #{}", orderId);
        log.info("Body: Payment for order #{} was rejected. Reason: {}. Please try again.", orderId, reason);
        log.info("=== END NOTIFICATION ===");
    }
}
