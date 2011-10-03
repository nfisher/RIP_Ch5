package com.restbucks.ordering.activities;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static com.restbucks.ordering.domain.PaymentBuilder.payment;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.RestbucksUri;


public class PaymentActivityTest {
    
    @Before
    @After
    public void clearRepositories() {
        PaymentRepository.current().clear();
        OrderRepository.current().clear();
    }
    
    @Test(expected = NoSuchOrderException.class)
    public void shouldFailForNonExistentOrder() {
        PaymentActivity activity = new PaymentActivity();
        
        RestbucksUri paymentUri = new RestbucksUri("http://restbucks.com/payment/does-not-exist");
        
        activity.pay(payment().build(), paymentUri);
    }

    @Test(expected = InvalidPaymentException.class)
    public void shouldFailForPaidOrder() {
      Identifier orderId = placeOrder(OrderStatus.PREPARING);
      
      PaymentActivity activity = new PaymentActivity();
      
      RestbucksUri paymentUri = new RestbucksUri("http://restbucks.com/payment/" + orderId.toString());
      
      activity.pay(payment().build(), paymentUri);
      
    }
    
    @Test(expected = InvalidPaymentException.class)
    public void shouldFailForPaymentWithIncorrectAmount() {
        Identifier orderId = placeOrder(OrderStatus.PREPARING);
        double orderAmount = OrderRepository.current().get(orderId).calculateCost();
        
        PaymentActivity activity = new PaymentActivity();
        
        RestbucksUri paymentUri = new RestbucksUri("http://restbucks.com/payment/" + orderId.toString());
        
        double wrongfulPaymentAmount = orderAmount + 99.9;
        activity.pay(payment().withAmount(wrongfulPaymentAmount).build(), paymentUri);
    }
    
    @Test
    public void shouldUpdateOrderStatusOnSuccessfulPayment() {
        Identifier orderId = placeOrder(OrderStatus.UNPAID);
        double orderAmount = OrderRepository.current().get(orderId).calculateCost();
        
        PaymentActivity activity = new PaymentActivity();
        
        RestbucksUri paymentUri = new RestbucksUri("http://restbucks.com/payment/" + orderId.toString());
        
        activity.pay(payment().withAmount(orderAmount).build(), paymentUri);
        
        assertEquals(OrderStatus.PREPARING, OrderRepository.current().get(orderId).getStatus());
        
    }
    
    private Identifier placeOrder(OrderStatus status) {
        Order order = order().withStatus(status).build();
        return OrderRepository.current().store(order);
    }
}
