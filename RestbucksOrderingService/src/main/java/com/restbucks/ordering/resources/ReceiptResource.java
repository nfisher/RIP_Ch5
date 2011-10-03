package com.restbucks.ordering.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.restbucks.ordering.activities.CompleteOrderActivity;
import com.restbucks.ordering.activities.NoSuchOrderException;
import com.restbucks.ordering.activities.OrderAlreadyCompletedException;
import com.restbucks.ordering.activities.OrderNotPaidException;
import com.restbucks.ordering.activities.ReadReceiptActivity;
import com.restbucks.ordering.domain.Identifier;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.ReceiptRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

@Path("/receipt")
public class ReceiptResource {

    private @Context
    UriInfo uriInfo;

    public ReceiptResource() {}

    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * 
     * @param uriInfo
     */
    public ReceiptResource(UriInfo uriInfo) {
        this.uriInfo = uriInfo;

    }

    @GET
    @Path("/{orderId}")
    @Produces("application/vnd.restbucks+xml")
    public Response getReceipt() {
        try {
            ReceiptRepresentation responseRepresentation = new ReadReceiptActivity().read(new RestbucksUri(uriInfo.getRequestUri()));
            return Response.ok().entity(responseRepresentation).build();
        } catch (OrderAlreadyCompletedException oce) {
            return Response.status(Status.NO_CONTENT).build();
        } catch (OrderNotPaidException onpe) {
            return Response.status(Status.NOT_FOUND).build();
        } catch (NoSuchOrderException nsoe) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
    
    @DELETE
    @Path("/{orderId}")
    public Response completeOrder(@PathParam("orderId")String identifier) {
        try {
            OrderRepresentation finalizedOrderRepresentation = new CompleteOrderActivity().completeOrder(new Identifier(identifier));
            return Response.ok().entity(finalizedOrderRepresentation).build();
        } catch (OrderAlreadyCompletedException oce) {
            return Response.status(Status.NO_CONTENT).build();
        } catch (NoSuchOrderException nsoe) {
                return Response.status(Status.NOT_FOUND).build();
        } catch (OrderNotPaidException onpe) {
            return Response.status(Status.CONFLICT).build();
        }
    }
}
