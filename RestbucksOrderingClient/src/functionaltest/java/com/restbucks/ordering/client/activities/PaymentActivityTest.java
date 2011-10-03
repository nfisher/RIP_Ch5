package com.restbucks.ordering.client.activities;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.client.TestHelper;
import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.repositories.OrderRepository;


public class PaymentActivityTest {
    
    @Before
    public void startServer() {
        try {
            TestHelper.getInstance().startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void stopServer() {
        TestHelper.getInstance().stopServer();
    }
    
    @Test
    public void shouldBeAbleToPayForAnOrder() throws Exception {
        Identifier identifier = placeOrder();
        double orderCost = OrderRepository.current().get(identifier).calculateCost();
        
        
        URI paymentUri = new URI(TestHelper.BASE_URI + "payment/" + identifier.toString());
        
        PaymentActivity activity = new PaymentActivity(paymentUri);
        activity.payForOrder(new Payment(orderCost, "Jonny Clash", "00774411", 12, nextYear()));
        Actions actions = activity.getActions();
        
        int numberOfPermittedActionsAfterPayment = 2;
        assertEquals(numberOfPermittedActionsAfterPayment, actions.size());
    }
    
    private Identifier placeOrder() {
        Order order = order().withRandomItems().build();
        Identifier identifier = OrderRepository.current().store(order);
        return identifier;
    }
    
    private int nextYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }
}
