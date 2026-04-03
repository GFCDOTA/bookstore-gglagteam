package com.bookstore.order.domain.port.out;

import com.bookstore.order.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
