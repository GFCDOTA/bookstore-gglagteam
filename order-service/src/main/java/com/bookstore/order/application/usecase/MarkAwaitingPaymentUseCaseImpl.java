package com.bookstore.order.application.usecase;

import com.bookstore.order.domain.port.in.MarkAwaitingPaymentUseCase;
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
public class MarkAwaitingPaymentUseCaseImpl implements MarkAwaitingPaymentUseCase {

    private final OrderRepository orderRepository;

    @Override
    public void execute(UUID orderId) {
        var order = orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        order.markAwaitingPayment();
        orderRepository.save(order);

        log.info("Order marked awaiting payment orderId={}", orderId);
    }
}
