package com.restbucks.ordering.domain;

import static com.restbucks.ordering.domain.ItemBuilder.item;

import java.util.ArrayList;

public class OrderBuilder {
    public static OrderBuilder order() {
        return new OrderBuilder();
    }

    private Location location = Location.TAKEAWAY;
    private ArrayList<Item> items = null;
    private OrderStatus status = OrderStatus.UNPAID;
    
    private void defaultItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(item().build());
        this.items = items;
    }
    
    private void corruptItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(null);
        items.add(null);
        items.add(null);
        items.add(null);
        this.items = items;
    }
   
    
    public Order build() {
        if(items == null) {
            defaultItems();
        }
        return new Order(location, status, items);
    }

    public OrderBuilder withItem(Item item) {
        if(items == null) {
            items = new ArrayList<Item>();
        }
        items.add(item);
        return this;
    }


    public OrderBuilder withCorruptedValues() {
        corruptItems();
        return this;
    }
    
    public OrderBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withRandomItems() {
        int numberOfItems = (int) (System.currentTimeMillis() % 10 + 1);
        this.items = new ArrayList<Item>();
        for(int i = 0; i < numberOfItems; i++) {
            items.add(item().random().build());
        }
        return this;
    }

}
