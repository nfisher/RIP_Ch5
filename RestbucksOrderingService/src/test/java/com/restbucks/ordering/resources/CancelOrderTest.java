package com.restbucks.ordering.resources;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.OrderRepresentation;


public class CancelOrderTest {
    
    private static final String baseOrderUri = "http://restbucks.com/order";
    private Identifier identifier;
    
    @Before
    public void createAnOrder() throws Exception {
        Order order = order().build();
        identifier = OrderRepository.current().store(order);
    }
    
    @After
    public void clearOutOldOrders() {
        OrderRepository.current().clear();
    }
    
    @Test
    public void shouldBeAbleToCancelAnOrderUsingDeleteOnItsUri() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseOrderUri + "/" + identifier.toString()));
        OrderResource orderResource = new OrderResource(mockUriInfo); 
        Response response = orderResource.removeOrder();
        
        assertEquals(200, response.getStatus());
    }
    
    @Test
    public void deletedOrderRepresentationsShouldContainNoLinks() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseOrderUri + "/" + identifier.toString()));
        OrderResource orderResource = new OrderResource(mockUriInfo); 
        Response response = orderResource.removeOrder();
        OrderRepresentation representation = OrderRepresentation.fromXmlString(response.getEntity().toString());
        assertNull(representation.getCancelLink());
        assertNull(representation.getPaymentLink());
        assertNull(representation.getUpdateLink());
    }
    
    @Test
    public void shouldNotBeAbleToCancelAnOrderThatDoesNotExist() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseOrderUri + "/does-not-exist"));
        OrderResource orderResource = new OrderResource(mockUriInfo); 
        Response response = orderResource.removeOrder();
        
        assertEquals(404, response.getStatus());
    }
    
    @Test
    public void shouldIndicateServerErrorWhenServerBarfsUnexpectedly() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenThrow(new RuntimeException("No really, nobody expects the Spanish inquisition!"));
        OrderResource orderResource = new OrderResource(mockUriInfo); 
        Response response = orderResource.removeOrder();
        
        assertEquals(500, response.getStatus());
    }
    
    @Test
    public void shouldErrorWhenCancellingAnOrderWhichHasBeenPaidButNotMade() throws Exception {
        OrderRepository.current().get(identifier).setStatus(OrderStatus.PREPARING);

        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseOrderUri + "/" + identifier.toString()));
        
        OrderResource orderResource = new OrderResource(mockUriInfo);
        Response response = orderResource.removeOrder();
        
        assertEquals(405, response.getStatus());
        
    }
}
