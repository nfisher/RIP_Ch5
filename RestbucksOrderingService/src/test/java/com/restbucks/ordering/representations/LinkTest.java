package com.restbucks.ordering.representations;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class LinkTest {
    @Test
    public void shouldBeAbleToDerserialiseLinksFromXml() throws Exception {
        String xmlLink = "<l:link xmlns:l=\""+ Representation.DAP_NAMESPACE + "\" rel=\"cancel\" uri=\"http://restbucks.com/order/4567\"/>";
        
        JAXBContext context = JAXBContext.newInstance(Link.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Link link = (Link) unmarshaller.unmarshal(new ByteArrayInputStream(xmlLink.getBytes()));
        
        assertEquals("cancel", link.getRelValue());
        assertEquals("http://restbucks.com/order/4567", link.getUri().toString());
    }
    
    @Test
    public void shouldBeAbleToSerialiseLinksToXml() throws Exception {
        Link link = new Link("update", new RestbucksUri("http://restbucks.com/order/1234"));
        
        JAXBContext context = JAXBContext.newInstance(Link.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(link, sw);
        
        assertThat(sw.toString(), containsString("mediaType=\"" + Representation.RESTBUCKS_MEDIA_TYPE + "\""));
        assertThat(sw.toString(), containsString("rel=\"update\""));
        assertThat(sw.toString(), containsString("uri=\"http://restbucks.com/order/1234\""));
    }
    
    @Test
    public void linksShouldBeAssociatedWithTheRestbucksMediaTypeByDefault() throws Exception {
        Link link = new Link("update", new RestbucksUri("http://restbucks.com/order/1234"));
        
        JAXBContext context = JAXBContext.newInstance(Link.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(link, sw);
        
        String linkString = sw.toString();
        assertThat(linkString, containsString("mediaType=\"" + Representation.RESTBUCKS_MEDIA_TYPE + "\""));
        assertEquals(1, linkString.split("mediaType=").length -1); // a single match will result in two strings in the array
        
    }
}
