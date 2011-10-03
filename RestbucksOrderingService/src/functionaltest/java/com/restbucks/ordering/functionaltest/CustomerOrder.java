package com.restbucks.ordering.functionaltest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.restbucks.ordering.activities.InvalidOrderException;
import com.restbucks.ordering.domain.Item;
import com.restbucks.ordering.domain.Location;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.representations.Representation;

@XmlRootElement(name = "order", namespace = Representation.RESTBUCKS_NAMESPACE)
public class CustomerOrder {
    
    @XmlElement(name = "item", namespace = Representation.RESTBUCKS_NAMESPACE)
    private List<Item> items;
    @XmlElement(name = "location", namespace = Representation.RESTBUCKS_NAMESPACE)
    private Location location;
    
    public CustomerOrder(){} // For JAXB
    
    public CustomerOrder(Location location, List<Item> items) {
        this.location = location;
        this.items = items;
    }
    
    public CustomerOrder(Order order) {
        this.location = order.getLocation();
        this.items = order.getItems();
    }

    public Location getLocation() {
        return location;
    }

    public List<Item> getItems() {
        return items;
    }
    
    public String toString() {
        try {
            JAXBContext context = JAXBContext.newInstance(CustomerOrder.class);
            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static CustomerOrder fromXmlString(String xmlString) {
        try {
            JAXBContext context = JAXBContext.newInstance(CustomerOrder.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (CustomerOrder) unmarshaller.unmarshal(new ByteArrayInputStream(xmlString.getBytes()));
        } catch (Exception e) {
            throw new InvalidOrderException(e);
        }
    }
}
