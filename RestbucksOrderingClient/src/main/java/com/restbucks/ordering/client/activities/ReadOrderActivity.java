package com.restbucks.ordering.client.activities;

import java.net.URI;

import com.restbucks.ordering.client.ClientOrder;
import com.restbucks.ordering.representations.OrderRepresentation;

public class ReadOrderActivity extends Activity {

    private final URI orderUri;
    private OrderRepresentation currentOrderRepresentation;

    public ReadOrderActivity(URI orderUri) {
        this.orderUri = orderUri;
    }

    public void readOrder() {
        try {
            currentOrderRepresentation = binding.retrieveOrder(orderUri);
            actions = new RepresentationHypermediaProcessor().extractNextActionsFromOrderRepresentation(currentOrderRepresentation);
        } catch (NotFoundException e) {
            actions = new Actions();
            actions.add(new PlaceOrderActivity());
        } catch (ServiceFailureException e) {
            actions = new Actions();
            actions.add(this);
        }
    }

    public ClientOrder getOrder() {
        return new ClientOrder(currentOrderRepresentation.getOrder());
    }
}
