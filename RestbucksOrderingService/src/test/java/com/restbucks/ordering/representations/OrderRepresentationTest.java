package com.restbucks.ordering.representations;

import static com.restbucks.ordering.domain.OrderBuilder.order;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OrderRepresentationTest {
    private static final String RELATIONS_URI = "http://relations.restbucks.com/";

    @Test
    public void shouldExposeLinksFromValidRepresentation() throws Exception {
        OrderRepresentation representation = new OrderRepresentation(order().build(), 
                new Link(RELATIONS_URI + "payment", new RestbucksUri("http://resbucks.com/payment/1234")),
                new Link(RELATIONS_URI + "cancel", new RestbucksUri("http://resbucks.com/order/1234")),
                new Link(RELATIONS_URI + "update", new RestbucksUri("http://resbucks.com/order/1234")),
                new Link(Representation.SELF_REL_VALUE, new RestbucksUri("http://resbucks.com/order/1234")));
        
        
        OrderRepresentation roundTripRepresentation = OrderRepresentation.fromXmlString(representation.toString());
        
        assertNotNull(roundTripRepresentation.getCancelLink());
        assertNotNull(roundTripRepresentation.getPaymentLink());
        assertNotNull(roundTripRepresentation.getUpdateLink());
        assertNotNull(roundTripRepresentation.getSelfLink());
    }
    
    @Test
    public void shouldExposeLinksFromValidStringRepresentation() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:order xmlns:ns2=\"" + Representation.RESTBUCKS_NAMESPACE + "\" xmlns:ns3=\"" + Representation.DAP_NAMESPACE + "\"><ns3:link uri=\"http://resbucks.com/order/1234\" rel=\"" + RELATIONS_URI +"latest\"/><ns3:link uri=\"http://resbucks.com/payment/1234\" rel=\"" + RELATIONS_URI +"payment\"/><ns3:link uri=\"http://resbucks.com/order/1234\" rel=\"" + RELATIONS_URI +"cancel\"/><ns3:link uri=\"http://resbucks.com/order/1234\" rel=\"" + RELATIONS_URI + "update\"/><ns2:item><ns2:milk>WHOLE</ns2:milk><ns2:size>SMALL</ns2:size><ns2:drink>LATTE</ns2:drink></ns2:item><ns2:location>TAKEAWAY</ns2:location></ns2:order>";

        OrderRepresentation representation = OrderRepresentation.fromXmlString(xmlString);
        
        assertNotNull(representation.getPaymentLink());
        assertNotNull(representation.getCancelLink());
        assertNotNull(representation.getUpdateLink());
    }
    
    @Test
    public void shouldExposeCostAndStatus() {
        OrderRepresentation representation = new OrderRepresentation(order().build(), 
                new Link(RELATIONS_URI + "payment", new RestbucksUri("http://resbucks.com/payment/1234")),
                new Link(RELATIONS_URI + "cancel", new RestbucksUri("http://resbucks.com/order/1234")),
                new Link(RELATIONS_URI + "update", new RestbucksUri("http://resbucks.com/order/1234")),
                new Link(Representation.SELF_REL_VALUE, new RestbucksUri("http://resbucks.com/order/1234")));

        assertThat(representation.toString(), containsString("status>"));
        assertThat(representation.toString(), containsString("cost>"));
        assertThat(representation.toString(), containsString("rel=\"" + Representation.SELF_REL_VALUE + "\""));
    }
}
