package com.restbucks.ordering.client;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static com.restbucks.ordering.domain.PaymentBuilder.payment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.client.activities.Actions;
import com.restbucks.ordering.client.activities.GetReceiptActivity;
import com.restbucks.ordering.client.activities.PaymentActivity;
import com.restbucks.ordering.client.activities.PlaceOrderActivity;
import com.restbucks.ordering.client.activities.ReadOrderActivity;
import com.restbucks.ordering.client.activities.UpdateOrderActivity;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.representations.Link;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.PaymentRepresentation;
import com.restbucks.ordering.representations.ReceiptRepresentation;
import com.sun.jersey.api.client.Client;

public class HappyPathTest {
    
    private static final String RESTBUCKS_MEDIA_TYPE = "application/vnd.restbucks+xml";

    @Before
    public void startServer() {
        try {
            TestHelper.getInstance().startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void stopServer() {
        TestHelper.getInstance().stopServer();
    }
    
    @Test
    public void shouldBeAbleToDriveTheProtocolThroughTheHappyStates() throws Exception {
        Order order = order().withRandomItems().build();
        
        PlaceOrderActivity placeOrderActivity = new PlaceOrderActivity();
        placeOrderActivity.placeOrder(order, new URI(TestHelper.BASE_URI + "order"));
        Actions actions =  placeOrderActivity.getActions();
        
        if(actions.has(UpdateOrderActivity.class)) {
            UpdateOrderActivity updateOrderActivity = actions.get(UpdateOrderActivity.class);
            updateOrderActivity.updateOrder(order);
            actions = updateOrderActivity.getActions();
        }
        
        ReadOrderActivity readOrderActivity = null;
        if(actions.has(ReadOrderActivity.class)) {
            readOrderActivity = actions.get(ReadOrderActivity.class);
            readOrderActivity.readOrder();
            actions = readOrderActivity.getActions();
            // Can we read the order resource?
            assertNotNull(readOrderActivity.getOrder().toString());
        }
        
        if(actions.has(PaymentActivity.class)) {
            PaymentActivity paymentActivity = actions.get(PaymentActivity.class);
            paymentActivity.payForOrder(payment().withAmount(readOrderActivity.getOrder().getCost()).build());
            actions = paymentActivity.getActions();
        }
        
        if(actions.has(GetReceiptActivity.class)) {
            GetReceiptActivity getReceiptActivity = actions.get(GetReceiptActivity.class);
            getReceiptActivity.getReceiptForOrder();
            actions = getReceiptActivity.getActions();
            assertNotNull(getReceiptActivity.getReceipt());
        }
    }
    
    @Test
    public void shouldBeAbleToDriveTheProtocolThroughTheHappyStatesUsingLinkDataOnly() throws Exception {
        Order order = order().withRandomItems().build();
        Client client = Client.create();
        OrderRepresentation orderRepresentation = client.resource(new URI(TestHelper.BASE_URI + "order")).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(OrderRepresentation.class, new OrderRepresentation(order));
        
        // Change the order
        order = order().withRandomItems().build();
        Link updateLink = orderRepresentation.getUpdateLink();
        OrderRepresentation updatedRepresentation = client.resource(updateLink.getUri()).accept(updateLink.getMediaType()).type(updateLink.getMediaType()).post(OrderRepresentation.class, new OrderRepresentation(order));
        
        // Pay for the order 
        Link paymentLink = updatedRepresentation.getPaymentLink();
        Payment payment = new Payment(updatedRepresentation.getCost(), "A.N. Other", "12345677878", 12, 2999);
        PaymentRepresentation  paymentRepresentation = client.resource(paymentLink.getUri()).accept(paymentLink.getMediaType()).type(paymentLink.getMediaType()).put(PaymentRepresentation.class, new PaymentRepresentation(payment));
        
        // Get a receipt
        Link receiptLink = paymentRepresentation.getReceiptLink();
        ReceiptRepresentation receiptRepresentation = client.resource(receiptLink.getUri()).get(ReceiptRepresentation.class);
        
        // Finally, check on the order status
        
        
        Link orderLink = receiptRepresentation.getOrderLink();
        OrderRepresentation finalOrderRepresentation = client.resource(orderLink.getUri()).accept(RESTBUCKS_MEDIA_TYPE).get(OrderRepresentation.class);
        assertEquals(OrderStatus.PREPARING, finalOrderRepresentation.getStatus());
    }
}
