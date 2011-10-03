package com.restbucks.ordering.resources;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Calendar;

import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.PaymentRepresentation;


public class PaymentResourceTest {
    
    private final String baseUri = "http://restbucks.com";
    private UriInfo mockUriInfo;
    private Identifier storedOrderIdentifier;
    private double orderCost;
    private String cardHoldername = "James K Polk";
    private String cardNumber = "777444888999";
    private int expiryMonth = 12;
    private int expiryYear = nextYear();
    
    private int nextYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }
    
    @Before
    public void setUpOrderDatabsaeAndUris() throws Exception {
        Order order = order().build();
        orderCost = order.calculateCost();
        storedOrderIdentifier = OrderRepository.current().store(order);
        
        mockUriInfo = mock(UriInfo.class);
        URI uri = new URI(baseUri + "/payment/" + storedOrderIdentifier.toString());
        when(mockUriInfo.getRequestUri()).thenReturn(uri);
    }

    @After
    public void resetTheDatabase() {
        OrderRepository.current().clear();
        PaymentRepository.current().clear();
    }
    
    @Test
    public void shouldBeAbleToLodgeAPayment() {
        PaymentResource theResource = new PaymentResource(mockUriInfo);
        PaymentRepresentation theRepresentation = new PaymentRepresentation(new Payment(orderCost, cardHoldername, cardNumber, expiryMonth, expiryYear));
        
        assertEquals(201, theResource.pay(theRepresentation).getStatus());
    }
}
