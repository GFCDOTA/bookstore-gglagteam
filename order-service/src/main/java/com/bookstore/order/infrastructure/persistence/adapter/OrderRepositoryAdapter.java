package com.bookstore.order.infrastructure.persistence.adapter;

import com.bookstore.order.domain.model.Order;
import com.bookstore.order.domain.model.OrderItem;
import com.bookstore.order.domain.model.OrderStatus;
import com.bookstore.order.domain.port.out.OrderRepository;
import com.bookstore.order.domain.vo.Money;
import com.bookstore.order.domain.vo.OrderId;
import com.bookstore.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.bookstore.order.infrastructure.persistence.entity.OrderJpaEntity;
import com.bookstore.order.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        var entity = toEntity(order);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    private OrderJpaEntity toEntity(Order order) {
        var entity = new OrderJpaEntity();
        entity.setId(order.id().value());
        entity.setCustomerId(order.customerId());
        entity.setStatus(OrderJpaEntity.OrderStatusJpa.valueOf(order.status().name()));
        entity.setTotalAmount(order.totalAmount().amount());
        entity.setTotalCurrency(order.totalAmount().currency().getCurrencyCode());

        var items = order.items().stream()
                .map(item -> {
                    var itemEntity = new OrderItemJpaEntity();
                    itemEntity.setOrder(entity);
                    itemEntity.setBookId(item.bookId());
                    itemEntity.setQuantity(item.quantity());
                    itemEntity.setUnitPrice(item.unitPrice().amount());
                    itemEntity.setUnitPriceCurrency(item.unitPrice().currency().getCurrencyCode());
                    return itemEntity;
                })
                .toList();

        entity.setItems(new java.util.ArrayList<>(items));
        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        var items = entity.getItems().stream()
                .map(itemEntity -> new OrderItem(
                        itemEntity.getBookId(),
                        itemEntity.getQuantity(),
                        new Money(itemEntity.getUnitPrice(), Currency.getInstance(itemEntity.getUnitPriceCurrency()))
                ))
                .toList();

        return new Order(
                OrderId.of(entity.getId()),
                entity.getCustomerId(),
                items,
                OrderStatus.valueOf(entity.getStatus().name()),
                new Money(entity.getTotalAmount(), Currency.getInstance(entity.getTotalCurrency()))
        );
    }
}
