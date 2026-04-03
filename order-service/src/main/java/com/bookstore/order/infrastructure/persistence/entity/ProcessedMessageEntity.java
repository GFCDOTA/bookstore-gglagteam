package com.bookstore.order.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "processed_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedMessageEntity {

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public static ProcessedMessageEntity of(String messageId) {
        return new ProcessedMessageEntity(messageId, Instant.now());
    }
}
