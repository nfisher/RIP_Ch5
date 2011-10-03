package com.restbucks.ordering.client.activities;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restbucks.ordering.client.TestHelper;
import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;


public class GetReceiptActivityTest {
    
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
    public void shouldBeAbleToRetreiveARecieptForPaidOrder() throws Exception {
        Identifier orderId = placeOrder();
        payForOrder(orderId);
        
        URI receiptUri = new URI(TestHelper.BASE_URI + "receipt/" + orderId.toString());
        
        GetReceiptActivity activity = new GetReceiptActivity(receiptUri);
        activity.getReceiptForOrder();
        Actions actions = activity.getActions();
        
        int numberOfActionsPermittedAfterGettingAReceipt = 1;
        assertEquals(numberOfActionsPermittedAfterGettingAReceipt , actions.size());
        
    }
    
    private void payForOrder(Identifier orderId) {
        Order order = OrderRepository.current().get(orderId);
        order.setStatus(OrderStatus.PREPARING);
        
        Payment payment = new Payment(order.calculateCost(), "Joe Strummer", "1952082120021222", 12, nextYear());
        PaymentRepository.current().store(orderId, payment);
    }

    private Identifier placeOrder() {
        Order order = order().withRandomItems().build();
        Identifier identifier = OrderRepository.current().store(order);
        return identifier;
    }
    
    private int nextYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }
}



