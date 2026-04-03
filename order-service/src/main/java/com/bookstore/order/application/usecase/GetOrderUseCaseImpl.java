package com.bookstore.order.application.usecase;

import com.bookstore.order.domain.port.in.GetOrderUseCase;
import com.bookstore.order.domain.port.out.OrderRepository;
import com.bookstore.order.domain.vo.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public Optional<OrderView> execute(UUID orderId) {
        return orderRepository.findById(OrderId.of(orderId))
                .map(order -> new OrderView(
                        order.id().value(),
                        order.customerId(),
                        order.status().name(),
                        order.totalAmount().amount(),
                        order.items().stream()
                                .map(item -> new OrderItemView(
                                        item.bookId(),
                                        item.quantity(),
                                        item.unitPrice().amount()
                                ))
                                .toList()
                ));
    }
}
