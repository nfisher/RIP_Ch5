package com.restbucks.ordering.functionaltest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.representations.Representation;

@XmlRootElement(name="payment", namespace = Representation.RESTBUCKS_NAMESPACE)
public class CustomerPayment {
    
    @XmlElement(namespace = Representation.RESTBUCKS_NAMESPACE) private double amount;
    @XmlElement(namespace = Representation.RESTBUCKS_NAMESPACE) private String cardholderName;
    @XmlElement(namespace = Representation.RESTBUCKS_NAMESPACE) private String cardNumber;
    @XmlElement(namespace = Representation.RESTBUCKS_NAMESPACE) private int expiryMonth;
    @XmlElement(namespace = Representation.RESTBUCKS_NAMESPACE) private int expiryYear;
    
    CustomerPayment(){} // For JAXB :-(
    
    public CustomerPayment(Payment payment) {
        amount = payment.getAmount();
        cardholderName = payment.getCardholderName();
        cardNumber = payment.getCardNumber();
        expiryMonth = payment.getExpiryMonth();
        expiryYear = payment.getExpiryYear();
    }
    
    public double getAmount() {
        return amount;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }
}