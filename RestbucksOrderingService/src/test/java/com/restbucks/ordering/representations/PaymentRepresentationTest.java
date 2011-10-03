package com.restbucks.ordering.representations;

import static com.restbucks.ordering.domain.PaymentBuilder.payment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PaymentRepresentationTest {

    @Test
    public void shouldProduceAValidXmlSerialisation() throws Exception {
        PaymentRepresentation representation = new PaymentRepresentation(payment().build(), 
                new Link("payment", new RestbucksUri("http://restbucks.com/payment/1234")), 
                new Link("order", new RestbucksUri("http://restbucks.com/order/1234")));

        JAXBContext context = JAXBContext.newInstance(PaymentRepresentation.class);
        Marshaller marshaller = context.createMarshaller();

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(representation, stringWriter);

        XPath xPath = XPathFactory.newInstance().newXPath();
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        Document doc = documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(stringWriter.toString().getBytes()));

        int numberOfPaymentElements = 1;
        assertEquals(numberOfPaymentElements, (((NodeList) xPath.compile("/payment").evaluate(doc, XPathConstants.NODESET)).getLength()));
        
        int numberOfCardholderNames = 1;
        assertEquals(numberOfCardholderNames, (((NodeList) xPath.compile("/payment/cardholderName").evaluate(doc, XPathConstants.NODESET)).getLength()));
        
        int numberOfLinks = 2;
        assertEquals(numberOfLinks, (((NodeList) xPath.compile("//link").evaluate(doc, XPathConstants.NODESET)).getLength()));
    }

    @Test
    public void shouldConsumeXmlSerialisationToPopulatePayment() throws Exception {
        double orderCost = 10.0;
        String paymentRepresentationXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><payment xmlns=\"" + Representation.RESTBUCKS_NAMESPACE + "\"><amount>" + orderCost + "</amount><cardholderName>A. N. Other</cardholderName><cardNumber>123456789</cardNumber><expiryMonth>12</expiryMonth><expiryYear>12</expiryYear></payment>";
        JAXBContext context = JAXBContext.newInstance(PaymentRepresentation.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        PaymentRepresentation paymentRepresentation = (PaymentRepresentation) unmarshaller.unmarshal(new ByteArrayInputStream(paymentRepresentationXmlString
                .getBytes()));

        assertNull(paymentRepresentation.links);
        assertNotNull(paymentRepresentation.getPayment());
        assertEquals(10.0, paymentRepresentation.getPayment().getAmount(), 0.0);

    }
}
