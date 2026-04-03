package com.bookstore.order.application.usecase;

import com.bookstore.order.domain.model.Order;
import com.bookstore.order.domain.model.OrderStatus;
import com.bookstore.order.domain.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.bookstore.order.domain.port.in.CreateOrderUseCase.OrderItemCommand;
import com.bookstore.order.domain.port.out.EventPublisher;
import com.bookstore.order.domain.port.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisher eventPublisher;

    private CreateOrderUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateOrderUseCaseImpl(orderRepository, eventPublisher);
    }

    @Test
    @DisplayName("should create order with AWAITING_STOCK status")
    void shouldCreateOrderWithAwaitingStockStatus() {
        var command = new CreateOrderCommand(
                UUID.randomUUID(),
                List.of(new OrderItemCommand(UUID.randomUUID(), 2, new BigDecimal("50.00")))
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertEquals(OrderStatus.AWAITING_STOCK.name(), result.status());
        assertNotNull(result.orderId());
    }

    @Test
    @DisplayName("should save order and publish OrderCreated event")
    void shouldSaveOrderAndPublishEvent() {
        var command = new CreateOrderCommand(
                UUID.randomUUID(),
                List.of(new OrderItemCommand(UUID.randomUUID(), 1, new BigDecimal("89.90")))
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    @DisplayName("should calculate total amount correctly for multiple items")
    void shouldCalculateTotalAmountCorrectly() {
        var command = new CreateOrderCommand(
                UUID.randomUUID(),
                List.of(
                        new OrderItemCommand(UUID.randomUUID(), 2, new BigDecimal("50.00")),
                        new OrderItemCommand(UUID.randomUUID(), 3, new BigDecimal("30.00"))
                )
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        var orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        var savedOrder = orderCaptor.getValue();
        assertEquals(new BigDecimal("190.00"), savedOrder.totalAmount().amount());
    }
}
