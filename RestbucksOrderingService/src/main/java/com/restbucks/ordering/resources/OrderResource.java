package com.restbucks.ordering.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.restbucks.ordering.activities.RemoveOrderActivity;
import com.restbucks.ordering.activities.CreateOrderActivity;
import com.restbucks.ordering.activities.InvalidOrderException;
import com.restbucks.ordering.activities.NoSuchOrderException;
import com.restbucks.ordering.activities.OrderDeletionException;
import com.restbucks.ordering.activities.ReadOrderActivity;
import com.restbucks.ordering.activities.UpdateException;
import com.restbucks.ordering.activities.UpdateOrderActivity;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

@Path("/order")
public class OrderResource {

    private @Context UriInfo uriInfo;

    public OrderResource() {
    }

    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * 
     * @param uriInfo
     */
    public OrderResource(UriInfo uriInfo) {
        this.uriInfo = uriInfo;  
    }
    
    @GET
    @Path("/{orderId}")
    @Produces("application/vnd.restbucks+xml")
    public Response getOrder() {
        try {
            OrderRepresentation responseRepresentation = new ReadOrderActivity().retrieveByUri(new RestbucksUri(uriInfo.getRequestUri()));
            return Response.ok().entity(responseRepresentation).build();
        } catch(NoSuchOrderException nsoe) {
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
    }
    
    @POST
    @Consumes("application/vnd.restbucks+xml")
    @Produces("application/vnd.restbucks+xml")
    public Response createOrder(String orderRepresentation) {
        try {
            OrderRepresentation responseRepresentation = new CreateOrderActivity().create(OrderRepresentation.fromXmlString(orderRepresentation).getOrder(), new RestbucksUri(uriInfo.getRequestUri()));
            return Response.created(responseRepresentation.getUpdateLink().getUri()).entity(responseRepresentation).build();
        } catch (InvalidOrderException ioe) {
            return Response.status(Status.BAD_REQUEST).build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{orderId}")
    @Produces("application/vnd.restbucks+xml")
    public Response removeOrder() {
        try {
            OrderRepresentation removedOrder = new RemoveOrderActivity().delete(new RestbucksUri(uriInfo.getRequestUri()));
            return Response.ok().entity(removedOrder).build();
        } catch (NoSuchOrderException nsoe) {
            return Response.status(Status.NOT_FOUND).build();
        } catch(OrderDeletionException ode) {
            return Response.status(405).header("Allow", "GET").build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/{orderId}")
    @Consumes("application/vnd.restbucks+xml")
    @Produces("application/vnd.restbucks+xml")
    public Response updateOrder(String orderRepresentation) {
        try {
            OrderRepresentation responseRepresentation = new UpdateOrderActivity().update(OrderRepresentation.fromXmlString(orderRepresentation).getOrder(), new RestbucksUri(uriInfo.getRequestUri()));
            return Response.ok().entity(responseRepresentation).build();
        } catch (InvalidOrderException ioe) {
            return Response.status(Status.BAD_REQUEST).build();
        } catch (NoSuchOrderException nsoe) {
            return Response.status(Status.NOT_FOUND).build();
        } catch(UpdateException ue) {
            return Response.status(Status.CONFLICT).build();
        } catch (Exception ex) {
            return Response.serverError().build();
        } 
     }
}
