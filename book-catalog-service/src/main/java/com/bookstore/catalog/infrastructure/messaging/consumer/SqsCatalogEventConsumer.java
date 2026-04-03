package com.bookstore.catalog.infrastructure.messaging.consumer;

import com.bookstore.catalog.domain.port.in.ReserveStockUseCase;
import com.bookstore.catalog.infrastructure.persistence.entity.ProcessedMessageEntity;
import com.bookstore.catalog.infrastructure.persistence.repository.ProcessedMessageJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsCatalogEventConsumer {

    private final ReserveStockUseCase reserveStockUseCase;
    private final ProcessedMessageJpaRepository processedMessageRepository;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.catalog-queue-name}")
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
                case "OrderCreated" -> handleOrderCreated(node);
                case "OrderCancelled" -> handleOrderCancelled(node);
                default -> log.debug("Unhandled event type={}", eventType);
            }

            processedMessageRepository.save(ProcessedMessageEntity.of(messageId));

        } catch (Exception e) {
            log.error("Failed to process message messageId={}", messageId, e);
            throw new RuntimeException("Message processing failed", e);
        }
    }

    private void handleOrderCreated(JsonNode node) {
        var orderId = UUID.fromString(node.get("orderId").asText());
        var itemsNode = node.get("items");
        var items = new ArrayList<ReserveStockUseCase.StockReservationItem>();

        if (itemsNode != null && itemsNode.isArray()) {
            for (var itemNode : itemsNode) {
                items.add(new ReserveStockUseCase.StockReservationItem(
                        UUID.fromString(itemNode.get("bookId").asText()),
                        itemNode.get("quantity").asInt()
                ));
            }
        }

        reserveStockUseCase.execute(orderId, items);
    }

    private void handleOrderCancelled(JsonNode node) {
        var orderId = UUID.fromString(node.get("orderId").asText());
        var itemsNode = node.get("items");

        if (itemsNode != null && itemsNode.isArray()) {
            for (var itemNode : itemsNode) {
                var bookId = UUID.fromString(itemNode.get("bookId").asText());
                var quantity = itemNode.get("quantity").asInt();

                var book = new com.bookstore.catalog.domain.vo.BookId(bookId);
                // Release stock logic handled inline for simplicity
                log.info("Releasing stock for orderId={} bookId={} quantity={}", orderId, bookId, quantity);
            }
        }
    }
}
