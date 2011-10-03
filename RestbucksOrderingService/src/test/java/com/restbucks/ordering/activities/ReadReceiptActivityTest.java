package com.restbucks.ordering.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderBuilder;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.ReceiptRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

public class ReadReceiptActivityTest {
    
    @Test
    public void shouldBeAbleToGenerateAReceiptForAPaidOrder() {
        Payment payment = new Payment(10.0, "Jimmy Jazz", "43534543543", 10, 50);
        Identifier identifier = PaymentRepository.current().store(payment);
        
        RestbucksUri receiptUri = new RestbucksUri("http://restbucks.com/receipt/" + identifier.toString());
        ReadReceiptActivity activity = new ReadReceiptActivity();
        ReceiptRepresentation representation = activity.read(receiptUri);
        
        assertEquals(10.0, representation.getAmountPaid(), 0.0);
        assertNotNull(representation.getPaidDate());
    }
    
    @Test(expected = OrderNotPaidException.class)
    public void shouldFailToGenerateAReceiptForAnUknowndOrder() {
        RestbucksUri receiptUri = new RestbucksUri("http://restbucks.com/payment/not-paid");
        ReadReceiptActivity activity = new ReadReceiptActivity();
        activity.read(receiptUri);
    }
}
