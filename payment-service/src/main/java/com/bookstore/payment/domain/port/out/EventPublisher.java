package com.bookstore.payment.domain.port.out;

import com.bookstore.payment.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
