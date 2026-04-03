package com.bookstore.order.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    @DisplayName("should create OrderId from UUID")
    void shouldCreateFromUuid() {
        var uuid = UUID.randomUUID();
        var orderId = OrderId.of(uuid);
        assertEquals(uuid, orderId.value());
    }

    @Test
    @DisplayName("should generate unique OrderIds")
    void shouldGenerateUnique() {
        var id1 = OrderId.generate();
        var id2 = OrderId.generate();
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("should throw when UUID is null")
    void shouldThrowWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> OrderId.of(null));
    }

    @Test
    @DisplayName("toString should return UUID string")
    void toStringShouldReturnUuidString() {
        var uuid = UUID.randomUUID();
        var orderId = OrderId.of(uuid);
        assertEquals(uuid.toString(), orderId.toString());
    }
}
