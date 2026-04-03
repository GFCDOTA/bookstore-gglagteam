package com.bookstore.order.domain.port.out;

import com.bookstore.order.domain.model.Order;
import com.bookstore.order.domain.vo.OrderId;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
}
