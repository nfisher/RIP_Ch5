package com.restbucks.ordering.resources;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.representations.OrderRepresentation;

public class PlaceOrderTest {

    private String baseUri = "http://restbucks.com/order";
    
    @Test
    public void shouldRespondPositivelyOnSuccessfulOrderCreation() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri));
        OrderResource orderResource = new OrderResource(mockUriInfo); 
        
        Order order = order().build();
        Response response = orderResource.createOrder(new OrderRepresentation(order).toString());
        
        String location = response.getMetadata().getFirst("Location").toString();
        
        assertEquals(201, response.getStatus());
        assertNotNull(location);
        assertTrue(location.startsWith(baseUri));
        assertTrue(location.length() > baseUri.length());
    }
    
    @Test
    public void shouldRespondWithOrderRepresentationOnSuccessfulOrderCreation() throws Exception {
         
        UriInfo mockUriInfo = mock(UriInfo.class);
        String baseUri = "http://restbucks.com/order";
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri));
        OrderResource orderResource = new OrderResource(mockUriInfo);
        
        Order order = order().build();
        Response response = orderResource.createOrder(new OrderRepresentation(order).toString());
        
        OrderRepresentation or = OrderRepresentation.fromXmlString(response.getEntity().toString());
        assertNotNull(or.getCancelLink());
        assertNotNull(or.getUpdateLink());
        assertNotNull(or.getPaymentLink());
        
        Order responseOrder = or.getOrder();
        assertNotNull(responseOrder);
        assertEquals(order.getItems().size(), responseOrder.getItems().size());
        
    }

    @Test
    public void shouldRespondWith500IfSomethingGoesWrong() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenThrow(new RuntimeException("No-one expects the Spanish inquisition!"));
        OrderResource orderResource = new OrderResource(mockUriInfo);
        
        Order order = order().build();
        Response response = orderResource.createOrder(new OrderRepresentation(order).toString());
        assertEquals(500, response.getStatus());
    }
    
    @Test
    public void shouldRespondWith400IfClientDoesSomethingWrong() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        String baseUri = "http://restbucks.com/order";
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri));
        OrderResource orderResource = new OrderResource(mockUriInfo);
        
        Order order = order().withCorruptedValues().build();
        
        Response response = orderResource.createOrder(new OrderRepresentation(order).toString());

        assertEquals(400, response.getStatus());
    }
}
