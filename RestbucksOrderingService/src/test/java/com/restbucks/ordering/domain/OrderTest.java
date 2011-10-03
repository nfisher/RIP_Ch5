package com.restbucks.ordering.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class OrderTest {
    @Test
    public void shouldStoreAndRetrieveItems() {
        int numberOfItems = 5;
        ArrayList<Item> items = new ArrayList<Item>();
        
        for(int i = 0; i < numberOfItems; i++) {
            items.add(new Item(Size.SMALL, Milk.WHOLE, Drink.LATTE));
        }
        
        Order order = new Order(Location.TAKEAWAY, OrderStatus.UNPAID, items);
        assertEquals(numberOfItems, order.getItems().size());
    }
    
    @Test
    public void shouldCalculateCost() {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item(Size.SMALL, Milk.NONE, Drink.ESPRESSO));
        items.add(new Item(Size.LARGE, Milk.WHOLE, Drink.LATTE));
        items.add(new Item(Size.LARGE, Milk.SEMI, Drink.CAPPUCCINO));
        
        Order order = new Order(Location.TAKEAWAY, items);
        order.calculateCost();
        assertEquals(5.5, order.calculateCost(), 0.0);
    }
}
