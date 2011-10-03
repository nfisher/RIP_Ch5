package com.restbucks.ordering.resources;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static com.restbucks.ordering.domain.PaymentBuilder.payment;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.domain.PaymentBuilder;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.PaymentRepresentation;

public class PaymentTest {

    private final String baseUri = "http://restbucks.com";
    private UriInfo mockUriInfo;
    private Identifier storedOrderIdentifier;
    private double orderCost;

    @Before
    public void setUpOrderDatabsaeAndUris() throws Exception {

        Order order = order().build();
        orderCost = order.calculateCost();
        storedOrderIdentifier = OrderRepository.current().store(order);

        mockUriInfo = mock(UriInfo.class);
        URI uri = new URI(baseUri + "/payment/" + storedOrderIdentifier.toString());
        when(mockUriInfo.getRequestUri()).thenReturn(uri);
        when(mockUriInfo.getBaseUri()).thenReturn(new URI(baseUri));
    }

    @After
    public void resetTheDatabase() {
        OrderRepository.current().clear();
        PaymentRepository.current().clear();
    }

    @Test
    public void shouldTakePaymentForAValidOrderOnly() throws Exception {
        PaymentResource paymentResource = new PaymentResource(mockUriInfo);
        Response response = paymentResource.pay(new PaymentRepresentation(payment().withAmount(orderCost).build()));

        assertEquals(201, response.getStatus());
    }

    @Test
    public void shouldNotTakePaymentForAPreviouslyPaidOrder() throws Exception {

        // This order's already been paid!
        Payment payment = payment().build();
        PaymentRepository.current().store(storedOrderIdentifier, payment);

        PaymentResource paymentResource = new PaymentResource(mockUriInfo);
        Response response = paymentResource.pay(new PaymentRepresentation(payment));
        assertEquals(403, response.getStatus());

    }

    @Test
    public void shouldRejectOrderIfPaymentInsufficient() throws Exception {
        PaymentResource paymentResource = new PaymentResource(mockUriInfo);
        Response response = paymentResource.pay(new PaymentRepresentation(PaymentBuilder.payment().withAmount(10.99f).build()));

        assertEquals(400, response.getStatus());
    }
}
