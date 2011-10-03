package com.restbucks.ordering.activities;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

public class UpdateOrderActivity {
    public OrderRepresentation update(Order order, RestbucksUri orderUri) {
        Identifier orderIdentifier = orderUri.getId();

        OrderRepository repository = OrderRepository.current();
        if (OrderRepository.current().orderNotPlaced(orderIdentifier)) { // Defensive check to see if we have the order
            throw new NoSuchOrderException();
        }

        if (!orderCanBeChanged(orderIdentifier)) {
            throw new UpdateException();
        }

        Order storedOrder = repository.get(orderIdentifier);
        
        storedOrder.setStatus(storedOrder.getStatus());
        storedOrder.calculateCost();


        return OrderRepresentation.createResponseOrderRepresentation(storedOrder, orderUri); 
    }
    
    private boolean orderCanBeChanged(Identifier identifier) {
        return OrderRepository.current().get(identifier).getStatus() == OrderStatus.UNPAID;
    }
}
