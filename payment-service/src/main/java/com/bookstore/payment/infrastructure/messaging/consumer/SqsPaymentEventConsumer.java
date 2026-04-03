package com.bookstore.payment.infrastructure.messaging.consumer;

import com.bookstore.payment.domain.port.in.ProcessPaymentUseCase;
import com.bookstore.payment.infrastructure.persistence.entity.ProcessedMessageEntity;
import com.bookstore.payment.infrastructure.persistence.repository.ProcessedMessageJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsPaymentEventConsumer {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ProcessedMessageJpaRepository processedMessageRepository;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.payment-queue-name}")
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

            if ("OrderCreated".equals(eventType)) {
                var orderId = UUID.fromString(node.get("orderId").asText());
                var totalAmount = new BigDecimal(node.get("totalAmount").asText());

                processPaymentUseCase.execute(orderId, totalAmount);
            } else {
                log.debug("Unhandled event type={}", eventType);
            }

            processedMessageRepository.save(ProcessedMessageEntity.of(messageId));

        } catch (Exception e) {
            log.error("Failed to process message messageId={}", messageId, e);
            throw new RuntimeException("Message processing failed", e);
        }
    }
}
