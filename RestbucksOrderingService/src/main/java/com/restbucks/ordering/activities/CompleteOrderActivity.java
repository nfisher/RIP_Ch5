package com.restbucks.ordering.activities;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.OrderRepresentation;

public class CompleteOrderActivity {

    public OrderRepresentation completeOrder(Identifier id) {
        OrderRepository repository = OrderRepository.current();
        if (repository.has(id)) {
            Order order = repository.get(id);

            if (order.getStatus() == OrderStatus.READY) {
                order.setStatus(OrderStatus.TAKEN);
            } else if (order.getStatus() == OrderStatus.TAKEN) {
                throw new OrderAlreadyCompletedException();
            } else if (order.getStatus() == OrderStatus.UNPAID) {
                throw new OrderNotPaidException();
            }

            return new OrderRepresentation(order);
        } else {
            throw new NoSuchOrderException();
        }
    }
}
