package com.restbucks.ordering.activities;

import org.junit.Test;

import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.*;

public class CreateOrderActivityTest {
    @Test
    public void shouldCreateUnpaidOrder() {
        CreateOrderActivity activity = new CreateOrderActivity();
        Order order = order().build();
        OrderRepresentation responseRepresentation = activity.create(order, new RestbucksUri("http://restbucks.com/order"));
        assertEquals(OrderStatus.UNPAID, responseRepresentation.getOrder().getStatus());
    }
}
