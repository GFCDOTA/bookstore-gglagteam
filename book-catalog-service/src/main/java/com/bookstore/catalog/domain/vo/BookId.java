package com.bookstore.catalog.domain.vo;

import java.util.UUID;

public record BookId(UUID value) {

    public static BookId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("BookId cannot be null");
        }
        return new BookId(value);
    }

    public static BookId generate() {
        return new BookId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
