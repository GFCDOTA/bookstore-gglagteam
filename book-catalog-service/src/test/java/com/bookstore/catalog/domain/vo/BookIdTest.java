package com.bookstore.catalog.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookIdTest {

    @Test
    @DisplayName("should create BookId from UUID")
    void shouldCreateFromUuid() {
        var uuid = UUID.randomUUID();
        var bookId = BookId.of(uuid);
        assertEquals(uuid, bookId.value());
    }

    @Test
    @DisplayName("should generate unique BookIds")
    void shouldGenerateUnique() {
        var id1 = BookId.generate();
        var id2 = BookId.generate();
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("should throw when UUID is null")
    void shouldThrowWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> BookId.of(null));
    }

    @Test
    @DisplayName("toString should return UUID string")
    void toStringShouldReturnUuidString() {
        var uuid = UUID.randomUUID();
        var bookId = BookId.of(uuid);
        assertEquals(uuid.toString(), bookId.toString());
    }
}
