package com.restbucks.ordering.representations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import com.restbucks.ordering.domain.Payment;


public class ReceiptRepresentationTest {
    
    private int nextYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }
    
    @Test
    public void validReceiptRepresentationsShouldHaveAmountDateAndOrderLink() {
        ReceiptRepresentation representation = new ReceiptRepresentation(new Payment(10.0, "Joe Strummer", "56565656565", 12, nextYear()), 
                new Link(Representation.RELATIONS_URI + "order", new RestbucksUri("http://restbucks.com/order/1234"), "GET"));
        
        assertEquals(10.0, representation.getAmountPaid(), 0.0);
        assertNotNull(representation.getPaidDate());
        assertTrue(representation.getPaidDate().isBeforeNow() || representation.getPaidDate().isEqualNow());
        assertNotNull(representation.getOrderLink());
    }
}
