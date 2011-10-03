package com.restbucks.ordering.client.activities;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.client.TestHelper;
import com.restbucks.ordering.domain.Order;


public class PlaceOrderActivityTest {

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
    public void shouldPlaceOrder() throws Exception {
        PlaceOrderActivity activity = new PlaceOrderActivity();
        
        Order order = order().withRandomItems().build();
        
        activity.placeOrder(order, new URI(TestHelper.BASE_URI + "order"));
        Actions actions = activity.getActions();
        
        int numberOfOPermissibleActionsFollowingSuccessfulCreation = 4;
        assertEquals(numberOfOPermissibleActionsFollowingSuccessfulCreation, actions.size());
    }
}
