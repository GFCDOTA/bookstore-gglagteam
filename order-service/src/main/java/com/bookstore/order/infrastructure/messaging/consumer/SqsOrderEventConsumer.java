package com.bookstore.order.infrastructure.messaging.consumer;

import com.bookstore.order.domain.port.in.CancelOrderUseCase;
import com.bookstore.order.domain.port.in.ConfirmOrderUseCase;
import com.bookstore.order.domain.port.in.MarkAwaitingPaymentUseCase;
import com.bookstore.order.infrastructure.persistence.entity.ProcessedMessageEntity;
import com.bookstore.order.infrastructure.persistence.repository.ProcessedMessageJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsOrderEventConsumer {

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final MarkAwaitingPaymentUseCase markAwaitingPaymentUseCase;
    private final ProcessedMessageJpaRepository processedMessageRepository;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.order-queue-name}")
    public void consume(Message<String> message) {
        var messageId = message.getHeaders().getId() != null
                ? message.getHeaders().getId().toString()
                : UUID.randomUUID().toString();

        if (processedMessageRepository.existsById(messageId)) {
            log.warn("Duplicate message skipped messageId={}", messageId);
            return;
        }

        try {
            var node = objectMapper.readTree(message.getPayload());
            var eventType = node.has("eventType") ? node.get("eventType").asText() : "";

            log.info("Event received eventType={} messageId={}", eventType, messageId);

            switch (eventType) {
                case "StockReserved" -> {
                    var orderId = UUID.fromString(node.get("orderId").asText());
                    markAwaitingPaymentUseCase.execute(orderId);
                }
                case "StockReservationFailed" -> {
                    var orderId = UUID.fromString(node.get("orderId").asText());
                    var reason = node.has("reason") ? node.get("reason").asText() : "Stock reservation failed";
                    cancelOrderUseCase.execute(orderId, reason);
                }
                case "PaymentApproved" -> {
                    var orderId = UUID.fromString(node.get("orderId").asText());
                    confirmOrderUseCase.execute(orderId);
                }
                case "PaymentRejected" -> {
                    var orderId = UUID.fromString(node.get("orderId").asText());
                    var reason = node.has("reason") ? node.get("reason").asText() : "Payment rejected";
                    cancelOrderUseCase.execute(orderId, reason);
                }
                default -> log.debug("Unhandled event type={}", eventType);
            }

            processedMessageRepository.save(ProcessedMessageEntity.of(messageId));

        } catch (Exception e) {
            log.error("Failed to process message messageId={}", messageId, e);
            throw new RuntimeException("Message processing failed", e);
        }
    }
}
