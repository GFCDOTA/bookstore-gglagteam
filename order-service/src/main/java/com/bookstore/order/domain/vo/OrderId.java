package com.bookstore.order.domain.vo;

import java.util.UUID;

public record OrderId(UUID value) {

    public static OrderId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
