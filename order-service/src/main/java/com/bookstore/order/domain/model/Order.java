package com.bookstore.order.domain.model;

import com.bookstore.order.domain.vo.Money;
import com.bookstore.order.domain.vo.OrderId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

    private final OrderId id;
    private final UUID customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Money totalAmount;

    public static Order create(UUID customerId, List<OrderItem> items) {
        var order = new Order(OrderId.generate(), customerId, new ArrayList<>(items));
        order.recalculateTotal();
        order.status = OrderStatus.AWAITING_STOCK;
        return order;
    }

    public Order(OrderId id, UUID customerId, List<OrderItem> items, OrderStatus status, Money totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.totalAmount = totalAmount;
    }

    private Order(OrderId id, UUID customerId, List<OrderItem> items) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
    }

    public void markAwaitingPayment() {
        assertNotFinalized();
        this.status = OrderStatus.AWAITING_PAYMENT;
    }

    public void confirm() {
        assertNotFinalized();
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        assertNotFinalized();
        this.status = OrderStatus.CANCELLED;
    }

    private void assertNotFinalized() {
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order " + id + " is already finalized with status " + status);
        }
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(item -> item.unitPrice().multiply(item.quantity()))
                .reduce(Money.ZERO, Money::add);
    }

    // Getters
    public OrderId id() { return id; }
    public UUID customerId() { return customerId; }
    public OrderStatus status() { return status; }
    public Money totalAmount() { return totalAmount; }
    public List<OrderItem> items() { return Collections.unmodifiableList(items); }
}
