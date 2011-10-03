package com.restbucks.ordering.activities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.restbucks.ordering.domain.OrderBuilder.order;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;


public class RemoveOrderActivityTest {
    
    @Before
    @After
    public void clearOrderAndPaymentRepositories() {
        OrderRepository.current().clear();
        PaymentRepository.current().clear();
    }
    
    @Test
    public void shouldCancelUnpaidOrder() {
        Order order = order().withStatus(OrderStatus.UNPAID).build();
        Identifier identifier = OrderRepository.current().store(order);
        
        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + identifier.toString());
        
        RemoveOrderActivity activity = new RemoveOrderActivity();
        
        OrderRepresentation deletedRepresentation = activity.delete(orderUri);
        
        assertNull(deletedRepresentation.getCancelLink());
        assertNull(deletedRepresentation.getUpdateLink());
        assertNull(deletedRepresentation.getSelfLink());
        assertNull(deletedRepresentation.getPaymentLink());
        assertNotNull(deletedRepresentation.getOrder());
    }
    
    @Test(expected = OrderDeletionException.class)
    public void shouldRejectCancellationForPaidOrderInPreparation() {
        Order order = order().withStatus(OrderStatus.PREPARING).build();
        Identifier identifier = OrderRepository.current().store(order);
        
        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + identifier.toString());
        
        RemoveOrderActivity activity = new RemoveOrderActivity();
        
        activity.delete(orderUri);
    }
    
    @Test(expected = OrderDeletionException.class)
    public void shouldNotAllowDeletionOfCollectableOrder() {
        Order order = order().withStatus(OrderStatus.READY).build();
        Identifier identifier = OrderRepository.current().store(order);
        
        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + identifier.toString());
        
        new RemoveOrderActivity().delete(orderUri);
    }
    
}
