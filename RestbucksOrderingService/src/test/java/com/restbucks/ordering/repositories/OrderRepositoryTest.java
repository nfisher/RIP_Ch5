package com.restbucks.ordering.repositories;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;


public class OrderRepositoryTest {
    
    @Test
    public void shouldShouldBeAbleToStoreAndRetrieveOrders() {
        OrderRepository or = OrderRepository.current();
        Order order = order().build();
        Identifier orderId = or.store(order);
        
        assertEquals(order, or.get(orderId));
    }
}
