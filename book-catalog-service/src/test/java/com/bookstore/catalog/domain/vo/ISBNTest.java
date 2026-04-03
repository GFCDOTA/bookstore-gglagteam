package com.bookstore.catalog.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ISBNTest {

    @Test
    @DisplayName("should accept valid ISBN-13")
    void shouldAcceptValidIsbn13() {
        var isbn = new ISBN("9780134494166");
        assertEquals("9780134494166", isbn.value());
    }

    @Test
    @DisplayName("should accept valid ISBN-10")
    void shouldAcceptValidIsbn10() {
        var isbn = new ISBN("0134494164");
        assertEquals("0134494164", isbn.value());
    }

    @Test
    @DisplayName("should accept ISBN with hyphens")
    void shouldAcceptIsbnWithHyphens() {
        assertDoesNotThrow(() -> new ISBN("978-0-13-449416-6"));
    }

    @Test
    @DisplayName("should accept ISBN with spaces")
    void shouldAcceptIsbnWithSpaces() {
        assertDoesNotThrow(() -> new ISBN("978 0 13 449416 6"));
    }

    @Test
    @DisplayName("should throw for null ISBN")
    void shouldThrowForNullIsbn() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN(null));
    }

    @Test
    @DisplayName("should throw for blank ISBN")
    void shouldThrowForBlankIsbn() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN("  "));
    }

    @Test
    @DisplayName("should throw for invalid ISBN format")
    void shouldThrowForInvalidIsbnFormat() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN("12345"));
    }

    @Test
    @DisplayName("should throw for ISBN with letters")
    void shouldThrowForIsbnWithLetters() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN("978013449ABC6"));
    }
}
