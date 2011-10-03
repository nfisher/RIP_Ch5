package com.restbucks.ordering.resources;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.restbucks.ordering.representations.OrderRepresentation;


public class ReadOrderTest {
    
    private static final String baseUri = "http://restbucks.com";
    
    @Test
    public void shouldFindExistingOrder() throws Exception {
        OrderRepresentation representation = placeOrder();
        
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(representation.getUpdateLink().getUri());
        
        OrderResource readResource = new OrderResource(mockUriInfo);
        assertNotNull(readResource.getOrder());
        
    }

    private OrderRepresentation placeOrder() throws URISyntaxException {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri + "/order"));
        OrderResource placeOrderResource = new OrderResource(mockUriInfo); 
        
        Response representation = placeOrderResource.createOrder(new OrderRepresentation(order().build()).toString());
        return (OrderRepresentation) representation.getEntity();
    }
    
    @Test
    public void shouldFailToFindNonExistentOrder() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri + "/order/1234567890"));
        
        OrderResource resource = new OrderResource(mockUriInfo);
        assertEquals(404, resource.getOrder().getStatus());
    }
}
