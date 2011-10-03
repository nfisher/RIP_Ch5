package com.restbucks.ordering.activities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.restbucks.ordering.activities.UriExchange;
import com.restbucks.ordering.representations.RestbucksUri;


public class UriExchangeTest {
    @Test
    public void shouldSwapOrderUriForPaymentUri() {
        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/1234");
        
        RestbucksUri paymentUri = UriExchange.paymentForOrder(orderUri);
        
        assertTrue(paymentUri.toString().endsWith("/payment/1234"));
    }
    
    @Test
    public void shouldSwapPaymentUriForOrderUri() {
        RestbucksUri paymentUri = new RestbucksUri("http://restbucks.com/payment/1234");
        
        RestbucksUri orderUri = UriExchange.orderForPayment(paymentUri);
        
        assertTrue(orderUri.toString().endsWith("/order/1234"));
        
    }
    
    @Test
    public void shouldSwapPaymentUriForReceiptUri() {
        RestbucksUri paymentUri = new RestbucksUri("http://restbucks.com/payment/1234");
        
        RestbucksUri receiptUri = UriExchange.receiptForPayment(paymentUri);
        
        assertTrue(receiptUri.toString().endsWith("/receipt/1234"));
    }
}
