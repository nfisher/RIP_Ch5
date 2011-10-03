package com.restbucks.ordering.resources;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static com.restbucks.ordering.domain.PaymentBuilder.payment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderBuilder;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.OrderRepresentation;


public class UpdateOrderTest {
    private String baseUri = "http://restbucks.com";
    
    @Test
    public void shouldNotFindOrderWithPreviouslyUnseenId() throws Exception {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri + "/order/" + UUID.randomUUID().toString()));
        
        OrderResource orderResource = new OrderResource(mockUriInfo);

        OrderRepresentation representation = new OrderRepresentation(order().build());
        
        Response response = orderResource.updateOrder(representation.toString());
       
        assertEquals(404, response.getStatus());
        assertNull("Location should be null", response.getMetadata().getFirst("Location"));
    }
    
    @Test
    public void shouldFailWithServerErrorIfServerGoesBang() {
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenThrow(new RuntimeException("No-one expects the Spanish inquisition!"));
        OrderResource orderResource = new OrderResource(mockUriInfo);
        
        Order order = order().build();
        Response response = orderResource.updateOrder(new OrderRepresentation(order).toString());
        assertEquals(500, response.getStatus());
    }
    
    @Test
    public void shouldFailWithConflictIfOrderCannotBeUpdated() throws Exception {
        Order order = OrderBuilder.order().withStatus(OrderStatus.PREPARING).build();
        Identifier identifier = OrderRepository.current().store(order);
        PaymentRepository.current().store(identifier, payment().build());
        
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri + "/order/" + identifier.toString()));
        
        OrderResource orderResource = new OrderResource(mockUriInfo);
        Response response = orderResource.updateOrder(new OrderRepresentation(order).toString());
        
        assertEquals(409, response.getStatus());
    }
    
    @Test
    public void shouldAcceptValidOrderUpdate() throws Exception {
        Order order = OrderBuilder.order().build();
        
        Identifier identifier = OrderRepository.current().store(order);
        
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri + "/order/" + identifier.toString()));
        
        OrderResource orderResource = new OrderResource(mockUriInfo);
        Response response = orderResource.updateOrder(new OrderRepresentation(order).toString());
        
        assertEquals(200, response.getStatus());   
    }
    
    @Test
    public void representationsForValidOrderUpdatesShouldContainPaymentAndUpdateLinks() throws Exception {
        Order order = OrderBuilder.order().build();
        Identifier identifier = OrderRepository.current().store(order);
        
        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getRequestUri()).thenReturn(new URI(baseUri + "/order/" + identifier.toString()));
        
        OrderResource orderResource = new OrderResource(mockUriInfo);
        Response response = orderResource.updateOrder(new OrderRepresentation(order).toString());
        
        
        OrderRepresentation result = OrderRepresentation.fromXmlString(response.getEntity().toString());
        
        assertNotNull(result.getCancelLink());
        assertNotNull(result.getPaymentLink());
        assertNotNull(result.getUpdateLink());
    }
}
