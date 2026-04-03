package com.bookstore.order.interfaces.rest.controller;

import com.bookstore.order.domain.port.in.CancelOrderUseCase;
import com.bookstore.order.domain.port.in.CreateOrderUseCase;
import com.bookstore.order.domain.port.in.GetOrderUseCase;
import com.bookstore.order.interfaces.exception.OrderNotFoundException;
import com.bookstore.order.interfaces.rest.dto.CreateOrderRequest;
import com.bookstore.order.interfaces.rest.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new CreateOrderUseCase.OrderItemCommand(i.bookId(), i.quantity(), i.unitPrice()))
                .toList();

        var command = new CreateOrderUseCase.CreateOrderCommand(request.customerId(), items);
        var result = createOrderUseCase.execute(command);
        return new CreateOrderResponse(result.orderId(), result.status());
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable UUID id) {
        return getOrderUseCase.execute(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        cancelOrderUseCase.execute(id, "Cancelled by customer");
    }

    public record CreateOrderResponse(UUID orderId, String status) {}
}
