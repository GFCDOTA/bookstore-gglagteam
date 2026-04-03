package com.bookstore.order.application.usecase;

import com.bookstore.order.domain.event.OrderCreatedEvent;
import com.bookstore.order.domain.model.Order;
import com.bookstore.order.domain.model.OrderItem;
import com.bookstore.order.domain.port.in.CreateOrderUseCase;
import com.bookstore.order.domain.port.out.EventPublisher;
import com.bookstore.order.domain.port.out.OrderRepository;
import com.bookstore.order.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Override
    public CreateOrderResult execute(CreateOrderCommand command) {
        var items = command.items().stream()
                .map(i -> new OrderItem(i.bookId(), i.quantity(), Money.of(i.unitPrice())))
                .toList();

        var order = Order.create(command.customerId(), items);
        var savedOrder = orderRepository.save(order);

        var eventItems = savedOrder.items().stream()
                .map(i -> new OrderCreatedEvent.OrderItemPayload(
                        i.bookId(), i.quantity(), i.unitPrice().amount()))
                .toList();

        eventPublisher.publish(OrderCreatedEvent.of(
                savedOrder.id().value(),
                savedOrder.customerId(),
                eventItems,
                savedOrder.totalAmount().amount()
        ));

        log.info("Order created orderId={} customerId={} totalAmount={}",
                savedOrder.id(), command.customerId(), savedOrder.totalAmount());

        return new CreateOrderResult(savedOrder.id().value(), savedOrder.status().name());
    }
}
