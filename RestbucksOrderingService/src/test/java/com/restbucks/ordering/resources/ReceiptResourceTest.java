package com.restbucks.ordering.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Calendar;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.ReceiptRepresentation;


public class ReceiptResourceTest {
    @Test
    public void shouldRetrieveAResourceWith200StatusCodeForAValidPaidOrder() throws Exception {
        Payment payment = new Payment(10.0, "JRR Tolkien", "46464646464", 12, nextYear());
        Identifier identifier = PaymentRepository.current().store(payment);
        
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("http://restbucks.com/receipt/" + identifier.toString()));
        
        ReceiptResource resource = new ReceiptResource(mockUriInfo);
        
        Response response = resource.getReceipt();
        
        assertEquals(200, response.getStatus());
        ReceiptRepresentation receiptRepresentation = (ReceiptRepresentation)response.getEntity();
        assertNotNull(receiptRepresentation);
        assertEquals(10.0, receiptRepresentation.getAmountPaid(), 0.0);
        assertNotNull(receiptRepresentation.getOrderLink());
        assertTrue(receiptRepresentation.getPaidDate().isBeforeNow());
        
    }
    
    @Test
    public void should404AnythingThatDoesNotHaveAValidPaidOrder() throws Exception {
        
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("http://restbucks.com/receipt/does-not-exist"));
        
        ReceiptResource resource = new ReceiptResource(mockUriInfo);
        
        Response response = resource.getReceipt();
        
        assertEquals(404, response.getStatus());
        
    }
    
    private int nextYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }
}
