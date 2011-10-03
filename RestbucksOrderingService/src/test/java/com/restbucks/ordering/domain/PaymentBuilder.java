package com.restbucks.ordering.domain;

public class PaymentBuilder {
    
    private double amount = 10.0f;
    private String cardholderName = "A. N. Other";
    private String cardNumber = "123456789";
    private int expiryMonth = 12;
    private int expiryYear = 12;
    
    public static PaymentBuilder payment() {
        return new PaymentBuilder();
    }
    
    public PaymentBuilder withAmount(double amount) {
        if(amount >= 0.0f) {
            this.amount = amount;
        }
        return this;
    }
    
    public PaymentBuilder withCardholderName(String name) {
        this.cardholderName = name;
        return this;
    }
    
    public PaymentBuilder withCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }
    
    public PaymentBuilder withExpiryMonth(int month) {
        if(month > 0 && month < 13) {
            this.expiryMonth= month;
        }
        return this;
    }
    
    public PaymentBuilder withExpiryYear(int year) {
        if(year >= 0) {
            this.expiryYear= year;
        }
        return this;
    }

    public Payment build() {
        return new Payment(amount, cardholderName, cardNumber, expiryMonth, expiryYear);
    }
}
