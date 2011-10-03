package com.restbucks.ordering.activities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.*;

public class ReadOrderActivityTest {

    @Before
    @After
    public void clearRepositories() {
        PaymentRepository.current().clear();
        OrderRepository.current().clear();
    }

    @Test(expected = NoSuchOrderException.class)
    public void shouldFailForNonExistentOrder() {
        ReadOrderActivity activity = new ReadOrderActivity();

        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/does-not-exist");
        activity.retrieveByUri(orderUri);
    }

    @Test
    public void shouldSucceedForExistingUnpaidOrder() {
        ReadOrderActivity activity = new ReadOrderActivity();

        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + placeOrder(OrderStatus.UNPAID).toString());
        OrderRepresentation orderRepresentation = activity.retrieveByUri(orderUri);

        assertNotNull(orderRepresentation);
        assertNotNull(orderRepresentation.getCancelLink());
        assertNotNull(orderRepresentation.getSelfLink());
        assertNotNull(orderRepresentation.getPaymentLink());
        assertNotNull(orderRepresentation.getUpdateLink());
    }
    
    @Test
    public void shouldSucceedForExistingPaidOrder() {
        ReadOrderActivity activity = new ReadOrderActivity();

        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + placeOrder(OrderStatus.PREPARING).toString());
        OrderRepresentation orderRepresentation = activity.retrieveByUri(orderUri);

        assertNotNull(orderRepresentation);
        assertNull(orderRepresentation.getCancelLink());
        assertNotNull(orderRepresentation.getSelfLink());
        assertNull(orderRepresentation.getPaymentLink());
        assertNull(orderRepresentation.getUpdateLink());
    }
    
    @Test
    public void shouldSucceedForExistingReadyOrder() {
        ReadOrderActivity activity = new ReadOrderActivity();

        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + placeOrder(OrderStatus.READY).toString());
        OrderRepresentation orderRepresentation = activity.retrieveByUri(orderUri);

        assertNotNull(orderRepresentation);
        assertNull(orderRepresentation.getCancelLink());
        assertNull(orderRepresentation.getSelfLink());
        assertNull(orderRepresentation.getPaymentLink());
        assertNull(orderRepresentation.getUpdateLink());
    }
    
    @Test
    public void shouldSucceedForExistingTakenOrder() {
        ReadOrderActivity activity = new ReadOrderActivity();

        RestbucksUri orderUri = new RestbucksUri("http://restbucks.com/order/" + placeOrder(OrderStatus.TAKEN).toString());
        OrderRepresentation orderRepresentation = activity.retrieveByUri(orderUri);

        assertNotNull(orderRepresentation);
        assertNull(orderRepresentation.getCancelLink());
        assertNull(orderRepresentation.getSelfLink());
        assertNull(orderRepresentation.getPaymentLink());
        assertNull(orderRepresentation.getUpdateLink());
    }

    private Identifier placeOrder(OrderStatus status) {
        return OrderRepository.current().store(order().withStatus(status).build());
    }
}
