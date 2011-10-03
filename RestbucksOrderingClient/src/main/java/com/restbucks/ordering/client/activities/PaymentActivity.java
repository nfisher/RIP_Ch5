package com.restbucks.ordering.client.activities;

import java.net.URI;

import com.restbucks.ordering.domain.Payment;
import com.restbucks.ordering.representations.PaymentRepresentation;

public class PaymentActivity extends Activity {

    private final URI paymentUri;
    private Payment payment;

    public PaymentActivity(URI paymentUri) {
        this.paymentUri = paymentUri;
    }

    public void payForOrder(Payment payment) {        
        try {
            PaymentRepresentation paymentRepresentation = binding.makePayment(payment, paymentUri);
            actions = new RepresentationHypermediaProcessor().extractNextActionsFromPaymentRepresentation(paymentRepresentation);
            payment = paymentRepresentation.getPayment();
        } catch (InvalidPaymentException e) {
            actions = retryCurrentActivity();
        } catch (NotFoundException e) {
            actions = noFurtherActivities();
        } catch (DuplicatePaymentException e) {
            actions = noFurtherActivities();
        } catch (ServiceFailureException e) {
            actions = retryCurrentActivity();            
        }
    }
    
    public Payment getPayment() {
        return payment;
    }
}
