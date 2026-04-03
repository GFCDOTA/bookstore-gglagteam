package com.bookstore.order.domain.model;

import com.bookstore.order.domain.vo.Money;

import java.util.UUID;

public record OrderItem(
        UUID bookId,
        int quantity,
        Money unitPrice
) {
    public OrderItem {
        if (bookId == null) {
            throw new IllegalArgumentException("BookId cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
    }
}
