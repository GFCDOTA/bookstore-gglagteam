package com.bookstore.catalog.domain.vo;

public record ISBN(String value) {

    public ISBN {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }
        String cleaned = value.replaceAll("[\\s-]", "");
        if (!cleaned.matches("^(\\d{10}|\\d{13})$")) {
            throw new IllegalArgumentException("Invalid ISBN format: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
