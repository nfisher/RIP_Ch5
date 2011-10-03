package com.restbucks.ordering.client.activities;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.*;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.client.TestHelper;
import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.repositories.OrderRepository;


public class TestReadOrderActivityTest {
    
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
    public void shouldBeAbleToReadPlacedOrder() throws Exception {
        Identifier identifier = placeOrder();
        URI orderUri = new URI(TestHelper.BASE_URI + "order/" + identifier.toString());
        
        ReadOrderActivity activity = new ReadOrderActivity(orderUri);
        activity.readOrder();
        Actions actions = activity.getActions();
        
        int numberOfActionsWithANewlyPlacedOrder = 4;
        assertEquals(numberOfActionsWithANewlyPlacedOrder , actions.size());
    }
    
    private Identifier placeOrder() {
        Order order = order().withRandomItems().build();
        Identifier identifier = OrderRepository.current().store(order);
        return identifier;
    }
}
