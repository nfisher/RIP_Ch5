package com.restbucks.ordering.client.activities;

import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.PaymentRepresentation;

class RepresentationHypermediaProcessor {

    Actions extractNextActionsFromOrderRepresentation(OrderRepresentation representation) {
        Actions actions = new Actions();

        if (representation != null) {

            if (representation.getPaymentLink() != null) {
                actions.add(new PaymentActivity(representation.getPaymentLink().getUri()));
            }

            if (representation.getUpdateLink() != null) {
                actions.add(new UpdateOrderActivity(representation.getUpdateLink().getUri()));
            }

            if (representation.getSelfLink() != null) {
                actions.add(new ReadOrderActivity(representation.getSelfLink().getUri()));
            }

            if (representation.getCancelLink() != null) {
                actions.add(new CancelOrderActivity(representation.getCancelLink().getUri()));
            }
        }

        return actions;
    }

    public Actions extractNextActionsFromPaymentRepresentation(PaymentRepresentation representation) {
        Actions actions = new Actions();
        
        if(representation.getOrderLink() != null) {
            actions.add(new ReadOrderActivity(representation.getOrderLink().getUri()));
        }
        
        if(representation.getReceiptLink() != null) {
            actions.add(new GetReceiptActivity(representation.getReceiptLink().getUri()));
        }
        
        return actions;
    }

}
