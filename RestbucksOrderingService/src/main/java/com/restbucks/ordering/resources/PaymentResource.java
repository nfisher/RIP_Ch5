package com.restbucks.ordering.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.restbucks.ordering.activities.InvalidPaymentException;
import com.restbucks.ordering.activities.NoSuchOrderException;
import com.restbucks.ordering.activities.PaymentActivity;
import com.restbucks.ordering.activities.UpdateException;
import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.representations.Link;
import com.restbucks.ordering.representations.PaymentRepresentation;
import com.restbucks.ordering.representations.Representation;
import com.restbucks.ordering.representations.RestbucksUri;

@Path("/payment/{paymentId}")
public class PaymentResource {
    
    private @Context UriInfo uriInfo;
    
    public PaymentResource(){}
    
    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * @param uriInfo
     */
    public PaymentResource(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @PUT
    @Consumes("application/vnd.restbucks+xml")
    @Produces("application/vnd.restbucks+xml")
    public Response pay(PaymentRepresentation paymentRepresentation) {
        try {
            return Response.created(uriInfo.getRequestUri()).entity(
                    new PaymentActivity().pay(paymentRepresentation.getPayment(), 
                            new RestbucksUri(uriInfo.getRequestUri()))).build();
        } catch(NoSuchOrderException nsoe) {
            return Response.status(Status.NOT_FOUND).build();
        } catch(UpdateException ue) {
            Identifier identifier = new RestbucksUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new RestbucksUri(uriInfo.getBaseUri().toString() + "order/" + identifier));
            return Response.status(Status.FORBIDDEN).entity(link).build();
        } catch(InvalidPaymentException ipe) {
            return Response.status(Status.BAD_REQUEST).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }
}
