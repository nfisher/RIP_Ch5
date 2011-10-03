package com.restbucks.ordering.activities;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.*;

import org.junit.Test;

import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

public class UpdateOrderActivityTest {
    
    @Test(expected = UpdateException.class)
    public void shouldNotAllowUpdatesOnPreparingOrders() {
        RestbucksUri orderUri = placeOrder(OrderStatus.PREPARING);
        
        UpdateOrderActivity activity = new UpdateOrderActivity();
        activity.update(order().withRandomItems().build(), orderUri);
    }
    
    @Test(expected = UpdateException.class)
    public void shouldNotAllowUpdatesOnReadyOrders() {
        RestbucksUri orderUri = placeOrder(OrderStatus.READY);
        
        UpdateOrderActivity activity = new UpdateOrderActivity();
        activity.update(order().withRandomItems().build(), orderUri);
    }
    
    @Test(expected = UpdateException.class)
    public void shouldNotAllowUpdatesOnTakenOrders() {
        RestbucksUri orderUri = placeOrder(OrderStatus.TAKEN);
        
        UpdateOrderActivity activity = new UpdateOrderActivity();
        activity.update(order().withRandomItems().build(), orderUri);
    }
    
    private RestbucksUri placeOrder(OrderStatus status) {
        return new RestbucksUri("http://restbucks.com/order/" + OrderRepository.current().store(order().withStatus(status).build()).toString());
    }

    @Test
    public void shouldAllowUpdatesOnUnpaidOrders() {
        RestbucksUri orderUri = placeOrder(OrderStatus.UNPAID);
        
        UpdateOrderActivity activity = new UpdateOrderActivity();
        OrderRepresentation updatedOrderRepresentation = activity.update(order().withRandomItems().build(), orderUri);
        
        assertNotNull(updatedOrderRepresentation.getCancelLink());
        assertNotNull(updatedOrderRepresentation.getUpdateLink());
        assertNotNull(updatedOrderRepresentation.getPaymentLink());
        assertNotNull(updatedOrderRepresentation.getSelfLink());
    }
}
