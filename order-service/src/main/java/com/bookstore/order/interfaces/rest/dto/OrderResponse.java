package com.bookstore.order.interfaces.rest.dto;

import com.bookstore.order.domain.port.in.GetOrderUseCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {
    public record OrderItemResponse(UUID bookId, int quantity, BigDecimal unitPrice) {}

    public static OrderResponse from(GetOrderUseCase.OrderView view) {
        return new OrderResponse(
                view.id(),
                view.customerId(),
                view.status(),
                view.totalAmount(),
                view.items().stream()
                        .map(i -> new OrderItemResponse(i.bookId(), i.quantity(), i.unitPrice()))
                        .toList()
        );
    }
}
