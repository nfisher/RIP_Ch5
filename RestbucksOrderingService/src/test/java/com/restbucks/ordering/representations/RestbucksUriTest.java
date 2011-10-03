package com.restbucks.ordering.representations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class RestbucksUriTest {
    @Test
    public void shouldReturnTheIdForAWellFormedRestbucksUri() {
        String uniqueOrderId = "fa6ea74b-8af1-4706-9f1c-9551fa6aff91";
        RestbucksUri restbucksUri = new RestbucksUri("http://restbucks.com/order/" + uniqueOrderId);
        
        assertEquals(uniqueOrderId, restbucksUri.getId().toString());
    }
    
    @Test
    public void shouldReturnTheBaseUri() {
        RestbucksUri myUri = new RestbucksUri("http://restbucks.com/order/1234");
        assertEquals("http://restbucks.com", myUri.getBaseUri());
        
        myUri = new RestbucksUri("http://restbucks.com:9999/order/5678");
        assertEquals("http://restbucks.com:9999", myUri.getBaseUri());
    }
}
