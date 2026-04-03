package com.bookstore.order.infrastructure.messaging.publisher;

import com.bookstore.order.domain.event.DomainEvent;
import com.bookstore.order.domain.port.out.EventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnsEventPublisher implements EventPublisher {

    private final SnsTemplate snsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.aws.sns.topic-arn}")
    private String topicArn;

    @Override
    public void publish(DomainEvent event) {
        try {
            var payload = objectMapper.writeValueAsString(event);
            var message = MessageBuilder.withPayload(payload)
                    .setHeader("eventType", event.eventType())
                    .build();

            snsTemplate.send(topicArn, message);

            log.info("Event published eventType={} aggregateId={} topicArn={}",
                    event.eventType(), event.aggregateId(), topicArn);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event eventType={}", event.eventType(), e);
            throw new RuntimeException("Failed to publish event: " + event.eventType(), e);
        }
    }
}
