package com.restbucks.ordering.functionaltest;

import static com.restbucks.ordering.domain.ItemBuilder.item;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.domain.Drink;
import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Item;
import com.restbucks.ordering.domain.Location;
import com.restbucks.ordering.domain.Milk;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.domain.Size;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.Link;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.PaymentRepresentation;
import com.restbucks.ordering.representations.ReceiptRepresentation;
import com.restbucks.ordering.representations.Representation;
import com.restbucks.ordering.representations.RestbucksUri;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class EndToEndTest {

    public static final String BASE_URI = "http://localhost:9998/";
    private static final String RESTBUCKS_MEDIA_TYPE = "application/vnd.restbucks+xml";

    private SelectorThread threadSelector;

    @Before
    public void startServer() throws Exception {
        final HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "com.restbucks.ordering.resources");

        threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
    }

    @After
    public void stopServer() {
        threadSelector.stopEndpoint();
    }

    @Test
    public void welcomeScreenSanityCheck() {
        Client client = Client.create();

        WebResource orderResource = client.resource(BASE_URI);
        String responseString = orderResource.get(String.class);
        assertNotNull(responseString);
        assertThat(responseString, containsString("<html>"));
        assertThat(responseString, containsString("Welcome to Restbucks"));
    }
    
    @Test
    public void shouldNotAcceptMalformattedOrders() {
        Client client = Client.create();
        WebResource orderResource = client.resource(BASE_URI + "order");

        ClientResponse response = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(ClientResponse.class, "<message>should not process me</message>");
        
        assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldBeAbleToPlaceAnOrder() {
        Client client = Client.create();

        WebResource orderResource = client.resource(BASE_URI + "order");
        
        Item item = new Item(Size.LARGE, Milk.SEMI, Drink.CAPPUCCINO);
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        
        CustomerOrder order = new CustomerOrder(Location.TAKEAWAY, items);

        ClientResponse response = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(ClientResponse.class, order);
        
        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        OrderRepresentation responseRepresentation = response.getEntity(OrderRepresentation.class);
        assertNotNull(responseRepresentation);
        
        assertNotNull(responseRepresentation.getCancelLink());
        assertNotNull(responseRepresentation.getPaymentLink());
        assertNotNull(responseRepresentation.getUpdateLink());
        assertNotNull(responseRepresentation.getSelfLink());
        
        assertTrue(responseRepresentation.getOrder().calculateCost() > 0.0);
        
        assertThat(responseRepresentation.toString(), containsString("status>"));
        assertThat(responseRepresentation.toString(), containsString("cost>"));

    }
    
    @Test
    public void shouldBeAbleToPlaceAndReadAnOrder() {
        OrderRepresentation placedOrder = placeOrder();
        
        Client client = Client.create();

        WebResource orderResource = client.resource(placedOrder.getSelfLink().getUri());
        
        
        ClientResponse response = orderResource.get(ClientResponse.class);
        
        assertEquals(200, response.getStatus());
        OrderRepresentation responseRepresentation = response.getEntity(OrderRepresentation.class);
        assertNotNull(responseRepresentation);
        assertNotNull(responseRepresentation.getCancelLink());
        assertNotNull(responseRepresentation.getPaymentLink());
        assertNotNull(responseRepresentation.getUpdateLink());
        assertNotNull(responseRepresentation.getSelfLink());
    }

    @Test
    public void shouldBeAbleToCancelAnOrderOnceOnly() {
        OrderRepresentation placedOrder = placeOrder();
        
        Client client = Client.create();

        WebResource orderResource = client.resource(placedOrder.getCancelLink().getUri());
        ClientResponse response = orderResource.delete(ClientResponse.class);
        
        assertEquals(200, response.getStatus());
        

        response = orderResource.delete(ClientResponse.class);
        assertEquals(404, response.getStatus());
        
    }
    
    @Test
    public void shouldBeAbleToUpdateAnOrderAnyNumberOfTimesBeforeCancellingAndNotAfterwards() {
        RestbucksUri orderUri = new RestbucksUri(placeOrder().getUpdateLink().getUri());
        
        int reasonableNumberOfUpdates = 10;
        ClientResponse response = null;
        for(int i = 0; i < reasonableNumberOfUpdates ; i++) {
            CustomerOrder order = CustomerOrder.fromXmlString(createRandomOrder().toString());
            
            response = updateOrder(orderUri, order);
            
            assertEquals(200, response.getStatus());
        }
        
        OrderRepresentation lastSuccessfullyPlacedOrderRepresentation = response.getEntity(OrderRepresentation.class);
        
        OrderRepresentation cancelledOrder = cancelOrder(lastSuccessfullyPlacedOrderRepresentation.getCancelLink());
        assertNull(cancelledOrder.getCancelLink());
        assertNull(cancelledOrder.getPaymentLink());
        assertNull(cancelledOrder.getUpdateLink());

        response = updateOrder(new RestbucksUri(lastSuccessfullyPlacedOrderRepresentation.getUpdateLink().getUri()), new CustomerOrder(lastSuccessfullyPlacedOrderRepresentation.getOrder()));
        assertEquals(404, response.getStatus());
        
    }
    
    @Test
    public void shouldBeAbleToPlaceAndPayForAnOrderOnceOnly() {
        OrderRepresentation orderRepresentation = placeOrder();
        
        Client client = Client.create();
        WebResource orderResource = client.resource(orderRepresentation.getPaymentLink().getUri());
        
        ClientResponse response = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(ClientResponse.class, createPayment(orderRepresentation.getOrder().calculateCost()));
        
        assertEquals(201, response.getStatus());
        
        response = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(ClientResponse.class, createPayment(orderRepresentation.getOrder().calculateCost()));
        
        assertNull(response.getMetadata().get("Allow"));
        assertEquals(403, response.getStatus());
        Link orderLink = response.getEntity(Link.class);
        assertThat(orderLink.getRelValue(), containsString(Representation.SELF_REL_VALUE));
        assertThat(orderLink.getUri().toString(), containsString("/order/"));
    }
    
    @Test
    public void shouldBeAbleToOrderPayAndGetReceiptThenWaitForOrderCompletion() {
        OrderRepresentation orderRepresentation = placeOrder();
        
        PaymentRepresentation paymentRepresentation = payForOrder(orderRepresentation);
        
        assertNotNull(paymentRepresentation);
        
        assertEquals(orderRepresentation.getSelfLink().getUri(), paymentRepresentation.getOrderLink().getUri());
        URI receiptUri = paymentRepresentation.getReceiptLink().getUri();
        assertNotNull(receiptUri);
        
        Client recipetClient = Client.create();        
        WebResource receiptResource = recipetClient.resource(receiptUri);
        ReceiptRepresentation receiptRepresentation = receiptResource.accept(RESTBUCKS_MEDIA_TYPE).get(ReceiptRepresentation.class);
        
        assertNotNull(receiptRepresentation.getPaidDate());
        URI orderUri = receiptRepresentation.getOrderLink().getUri();
        assertNotNull(orderUri);
        
        // Poll the order until it becomes READY
        Client orderClient = Client.create();
        WebResource polledOrderResource = orderClient.resource(orderUri);
        
        OrderRepresentation polledRepresentation = polledOrderResource.accept(RESTBUCKS_MEDIA_TYPE).get(OrderRepresentation.class);
        assertEquals(OrderStatus.PREPARING, polledRepresentation.getOrder().getStatus());
        
        URI polledOrderUri = polledRepresentation.getSelfLink().getUri();
        while(polledRepresentation.getOrder().getStatus() != OrderStatus.READY) {
            polledRepresentation = polledOrderResource.accept(RESTBUCKS_MEDIA_TYPE).get(OrderRepresentation.class);
            pokeTheBarista(polledOrderUri);
        }
    
        // Finally take the order
        ClientResponse receiptResponse = receiptResource.type(RESTBUCKS_MEDIA_TYPE).delete(ClientResponse.class);
        assertEquals(200, receiptResponse.getStatus());

        OrderRepresentation finalOrderRepresentation = receiptResponse.getEntity(OrderRepresentation.class);
        assertNotNull(finalOrderRepresentation);
        assertNotNull(finalOrderRepresentation.getOrder());
        assertNull(finalOrderRepresentation.getCancelLink());
        assertNull(finalOrderRepresentation.getSelfLink());
        assertNull(finalOrderRepresentation.getUpdateLink());
        assertNull(finalOrderRepresentation.getPaymentLink());
        
        
        //Make sure receipt is still around, but without hyperlinks        
        receiptResource = recipetClient.resource(receiptUri);
        receiptResponse = receiptResource.accept(RESTBUCKS_MEDIA_TYPE).get(ClientResponse.class);
        assertEquals(204, receiptResponse.getStatus());
        
    }
    
    private void pokeTheBarista(URI orderUri) {
        Identifier identifier = new RestbucksUri(orderUri).getId();
        OrderRepository.current().get(identifier).setStatus(OrderStatus.READY);
    }

    @Test
    public void shouldNotBeAbleToCancelPaidOrder() {
        OrderRepresentation orderRepresentation = placeOrder();
        payforOrder(orderRepresentation);
        
        Client client = Client.create();
        WebResource orderResource = client.resource(orderRepresentation.getCancelLink().getUri());
        ClientResponse response = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).delete(ClientResponse.class);
        
        assertEquals(405, response.getStatus());
        List<String> allowedVerbs = response.getMetadata().get("Allow");
        assertNotNull(allowedVerbs);
        assertEquals(1, allowedVerbs.size());
    }
    
    @Test
    public void shouldBeAbleToObtainTheRepresentationOfARecentlyPlacedOrder() {
        RestbucksUri orderUri = new RestbucksUri(placeOrder().getUpdateLink().getUri());
        
        Client client = Client.create();
        WebResource orderResource = client.resource(orderUri.getFullUri());
        
        ClientResponse response = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).get(ClientResponse.class);
        
        assertEquals(200, response.getStatus());
        OrderRepresentation responseRepresentation = response.getEntity(OrderRepresentation.class);
        assertNotNull(responseRepresentation);
    }

    private PaymentRepresentation payForOrder(OrderRepresentation orderRepresentation) {
        Client client = Client.create();
        
        WebResource orderResource = client.resource(orderRepresentation.getPaymentLink().getUri());
        
        PaymentRepresentation put = orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(PaymentRepresentation.class, createPayment(orderRepresentation.getOrder().calculateCost()));
        
        return put;
    }
    
    private void payforOrder(OrderRepresentation orderRepresentation) {
        Client client = Client.create();
        WebResource orderResource = client.resource(orderRepresentation.getPaymentLink().getUri());
        
        orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(ClientResponse.class, createPayment(orderRepresentation.getOrder().calculateCost()));
    }

    private CustomerPayment createPayment(double cost) {
        return new CustomerPayment(new Payment(cost, "Michael Farraday", "11223344", 12, 12));
    }

    private OrderRepresentation cancelOrder(Link cancelLink) {
        Client client = Client.create();
        WebResource orderResource = client.resource(cancelLink.getUri().toString());
        return orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).delete(OrderRepresentation.class);
    }

    private ClientResponse updateOrder(RestbucksUri orderUri, CustomerOrder order) {
        Client client = Client.create();
        WebResource orderResource = client.resource(orderUri.toString());
        return orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(ClientResponse.class, order);
    }
    

    private CustomerOrder createRandomOrder() {        
        int numberOfItemsInOrder = (int) (System.currentTimeMillis() % 10) + 1; // Always at least one!
        List<Item> items = new ArrayList<Item>();
        
        for(int i = 0; i < numberOfItemsInOrder; i ++) {
            items.add(item().random().build());
        }
        
        return new CustomerOrder(Location.IN_STORE, items);
    }

    

    private OrderRepresentation placeOrder() {
        Client client = Client.create();

        WebResource orderResource = client.resource(BASE_URI + "order");
        
        Item item = new Item(Size.LARGE, Milk.SEMI, Drink.CAPPUCCINO);
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        
        
        CustomerOrder order = new CustomerOrder(Location.TAKEAWAY, items);
        
        return orderResource.accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(OrderRepresentation.class, order);

    }
}
