package com.restbucks.ordering.client;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.restbucks.ordering.domain.Item;
import com.restbucks.ordering.domain.Location;
import com.restbucks.ordering.domain.Order;
import com.restbucks.ordering.domain.OrderStatus;
import com.restbucks.ordering.representations.Representation;

@XmlRootElement(name = "order", namespace = Representation.RESTBUCKS_NAMESPACE)
public class ClientOrder {
    
    @XmlElement(name = "item", namespace = Representation.RESTBUCKS_NAMESPACE)
    private List<Item> items;
    @XmlElement(name = "location", namespace = Representation.RESTBUCKS_NAMESPACE)
    private Location location;
    @XmlElement(name = "status", namespace = Representation.RESTBUCKS_NAMESPACE)
    private OrderStatus status;
    
    private ClientOrder(){}
    
    public ClientOrder(Order order) {
        this.location = order.getLocation();
        this.items = order.getItems();
    }
    
    public Order getOrder() {
        return new Order(location, status, items);
    }
    
    public Location getLocation() {
        return location;
    }
    
    public List<Item> getItems() {
        return items;
    }

    public String toString() {
        try {
            JAXBContext context = JAXBContext.newInstance(ClientOrder.class);
            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public OrderStatus getStatus() {
        return status;
    }

    public double getCost() {
        double total = 0.0;
        if (items != null) {
            for (Item item : items) {
                if(item != null && item.getDrink() != null) {
                    total += item.getDrink().getPrice();
                }
            }
        }
        return total;
    }
}