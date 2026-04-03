package com.bookstore.order.application.usecase;

import com.bookstore.order.domain.event.OrderCancelledEvent;
import com.bookstore.order.domain.port.in.CancelOrderUseCase;
import com.bookstore.order.domain.port.out.EventPublisher;
import com.bookstore.order.domain.port.out.OrderRepository;
import com.bookstore.order.domain.vo.OrderId;
import com.bookstore.order.interfaces.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Override
    public void execute(UUID orderId, String reason) {
        var order = orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        order.cancel(reason);
        orderRepository.save(order);

        var cancelledItems = order.items().stream()
                .map(item -> new OrderCancelledEvent.OrderCancelledItem(item.bookId(), item.quantity()))
                .toList();

        eventPublisher.publish(OrderCancelledEvent.of(orderId, order.customerId(), cancelledItems));

        log.info("Order cancelled orderId={} reason={}", orderId, reason);
    }
}
