package com.restbucks.ordering.client.network;

import java.net.URI;

import com.restbucks.ordering.client.activities.CannotCancelException;
import com.restbucks.ordering.client.activities.CannotUpdateOrderException;
import com.restbucks.ordering.client.activities.DuplicatePaymentException;
import com.restbucks.ordering.client.activities.InvalidPaymentException;
import com.restbucks.ordering.client.activities.MalformedOrderException;
import com.restbucks.ordering.client.activities.NotFoundException;
import com.restbucks.ordering.client.activities.ServiceFailureException;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.PaymentRepresentation;
import com.restbucks.ordering.representations.ReceiptRepresentation;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class HttpBinding {

    private static final String RESTBUCKS_MEDIA_TYPE = "application/vnd.restbucks+xml";

    public OrderRepresentation createOrder(Order order, URI orderUri) throws MalformedOrderException, ServiceFailureException {
        Client client = Client.create();
        ClientResponse response = client.resource(orderUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(ClientResponse.class, new OrderRepresentation(order));

        int status = response.getStatus();

        if (status == 400) {
            throw new MalformedOrderException();
        } else if (status == 500) {
            throw new ServiceFailureException();
        } else if (status == 201) {
            return response.getEntity(OrderRepresentation.class);
        }

        throw new RuntimeException(String.format("Unexpected response [%d] while creating order resource [%s]", status, orderUri.toString()));
    }
    
    public OrderRepresentation retrieveOrder(URI orderUri) throws NotFoundException, ServiceFailureException {
        Client client = Client.create();
        ClientResponse response = client.resource(orderUri).accept(RESTBUCKS_MEDIA_TYPE).get(ClientResponse.class);

        int status = response.getStatus();

        if (status == 404) {
            throw new NotFoundException ();
        } else if (status == 500) {
            throw new ServiceFailureException();
        } else if (status == 200) {
            return response.getEntity(OrderRepresentation.class);
        }

        throw new RuntimeException(String.format("Unexpected response while retrieving order resource [%s]", orderUri.toString()));
    }

    public OrderRepresentation updateOrder(Order order, URI orderUri) throws MalformedOrderException, ServiceFailureException, NotFoundException,
            CannotUpdateOrderException {
        Client client = Client.create();
        ClientResponse response = client.resource(orderUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(ClientResponse.class, new OrderRepresentation(order));

        int status = response.getStatus();

        if (status == 400) {
            throw new MalformedOrderException();
        } else if (status == 404) {
            throw new NotFoundException();
        } else if (status == 409) {
            throw new CannotUpdateOrderException();
        } else if (status == 500) {
            throw new ServiceFailureException();
        } else if (status == 200) {
            return response.getEntity(OrderRepresentation.class);
        }

        throw new RuntimeException(String.format("Unexpected response [%d] while udpating order resource [%s]", status, orderUri.toString()));
    }

    public OrderRepresentation deleteOrder(URI orderUri) throws ServiceFailureException, CannotCancelException, NotFoundException {
        Client client = Client.create();
        ClientResponse response = client.resource(orderUri).accept(RESTBUCKS_MEDIA_TYPE).delete(ClientResponse.class);

        int status = response.getStatus();
        if (status == 404) {
            throw new NotFoundException();
        } else if (status == 405) {
            throw new CannotCancelException();
        } else if (status == 500) {
            throw new ServiceFailureException();
        } else if (status == 200) {
            return response.getEntity(OrderRepresentation.class);
        }

        throw new RuntimeException(String.format("Unexpected response [%d] while deleting order resource [%s]", status, orderUri.toString()));
    }

    public PaymentRepresentation makePayment(Payment payment, URI paymentUri) throws InvalidPaymentException, NotFoundException, DuplicatePaymentException,
            ServiceFailureException {
        Client client = Client.create();
        ClientResponse response = client.resource(paymentUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(ClientResponse.class, new PaymentRepresentation(payment));

        int status = response.getStatus();
        if (status == 400) {
            throw new InvalidPaymentException();
        } else if (status == 404) {
            throw new NotFoundException();
        } else if (status == 405) {
            throw new DuplicatePaymentException();
        } else if (status == 500) {
            throw new ServiceFailureException();
        } else if (status == 201) {
            return response.getEntity(PaymentRepresentation.class);
        }

        throw new RuntimeException(String.format("Unexpected response [%d] while creating payment resource [%s]", status, paymentUri.toString()));
    }

    public ReceiptRepresentation retrieveReceipt(URI receiptUri) throws NotFoundException, ServiceFailureException {
        Client client = Client.create();
        ClientResponse response = client.resource(receiptUri).accept(RESTBUCKS_MEDIA_TYPE).get(ClientResponse.class);

        int status = response.getStatus();
        if (status == 404) {
            throw new NotFoundException();
        } else if (status == 500) {
            throw new ServiceFailureException();
        } else if (status == 200) {
            return response.getEntity(ReceiptRepresentation.class);
        }
        
        throw new RuntimeException(String.format("Unexpected response [%d] while retrieving receipt resource [%s]", status, receiptUri.toString()));
    }
}
