package com.restbucks.ordering.client.activities;

import static org.junit.Assert.*;

import org.junit.Test;


public class ActionsTest {
    @Test
    public void shouldBeAbleToAskWhichActionsAreAvailable() {
       Actions actions = fillActions(); 
       
       assertTrue(actions.has(ReadOrderActivity.class));
       assertTrue(actions.has(UpdateOrderActivity.class));
       assertFalse(actions.has(PlaceOrderActivity.class));
    }
    
    @Test
    public void shouldBeAbleToRetreiveAnAction() {
        final Actions actions = fillActions();
        
        assertNotNull(actions.get(ReadOrderActivity.class));
        assertNull(actions.get(PlaceOrderActivity.class));
    }

    private Actions fillActions() {
        Actions actions = new Actions();
        actions.add(new UpdateOrderActivity(null));
        actions.add(new ReadOrderActivity(null));
        return actions;
    }
}
