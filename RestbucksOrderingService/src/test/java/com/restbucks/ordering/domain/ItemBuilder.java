package com.restbucks.ordering.domain;

import java.util.Random;


public class ItemBuilder {
    public static ItemBuilder item() {
        return new ItemBuilder();
    }

    private Milk milk = Milk.WHOLE;    
    private Drink drink = Drink.LATTE;
    private Size size = Size.SMALL;
    
    public Item build() {
        return new Item(size, milk, drink);
    }
    
    public ItemBuilder withMilk(Milk milk) {
        this.milk  = milk;
        return this;
    }
    
    public ItemBuilder withDrink(Drink drink) {
        this.drink = drink;
        return this;
    }
    
    public ItemBuilder withSize(Size size) {
        this.size = size;
        return this;
    }

    public ItemBuilder random() {
        do {
        Random r = new Random();
        size = Size.values()[r.nextInt(Size.values().length)];
        drink = Drink.values()[r.nextInt(Drink.values().length)];
        milk = Milk.values()[r.nextInt(Milk.values().length)];
        } while((drink == Drink.ESPRESSO && milk != Milk.NONE) || (drink != Drink.ESPRESSO && milk == Milk.NONE)); // Obviously Espresso doesn't have milk, other drinks do!
        
        return this;
    }
}
