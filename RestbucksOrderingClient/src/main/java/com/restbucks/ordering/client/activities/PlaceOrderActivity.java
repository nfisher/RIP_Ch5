package com.restbucks.ordering.client.activities;

import java.net.URI;

import com.restbucks.ordering.client.ClientOrder;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.representations.OrderRepresentation;

public class PlaceOrderActivity extends Activity {

    private Order order;

    public void placeOrder(Order order, URI orderingUri) {
        
        try {
            OrderRepresentation createdOrderRepresentation = binding.createOrder(order, orderingUri);
            this.actions = new RepresentationHypermediaProcessor().extractNextActionsFromOrderRepresentation(createdOrderRepresentation);
            this.order = createdOrderRepresentation.getOrder();
        } catch (MalformedOrderException e) {
            this.actions = retryCurrentActivity();
        } catch (ServiceFailureException e) {
            this.actions = retryCurrentActivity();
        }
    }
    
    public ClientOrder getOrder() {
        return new ClientOrder(order);
    }
}
