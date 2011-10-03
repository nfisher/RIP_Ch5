package com.restbucks.ordering.activities;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.Link;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.Representation;
import com.restbucks.ordering.representations.RestbucksUri;

public class CreateOrderActivity {
    public OrderRepresentation create(Order order, RestbucksUri requestUri) {
        order.setStatus(OrderStatus.UNPAID);
                
        Identifier identifier = OrderRepository.current().store(order);
        
        RestbucksUri orderUri = new RestbucksUri(requestUri.getBaseUri() + "/order/" + identifier.toString());
        RestbucksUri paymentUri = new RestbucksUri(requestUri.getBaseUri() + "/payment/" + identifier.toString());
        return new OrderRepresentation(order, 
                new Link(Representation.RELATIONS_URI + "cancel", orderUri), 
                new Link(Representation.RELATIONS_URI + "payment", paymentUri), 
                new Link(Representation.RELATIONS_URI + "update", orderUri),
                new Link(Representation.SELF_REL_VALUE, orderUri));
    }
}
