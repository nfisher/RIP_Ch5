package com.restbucks.ordering.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class WelcomeResource {

    private @Context UriInfo uriInfo;
    
    public WelcomeResource(){}
    
    @GET
    @Produces("text/html")
    public Response welcomeCustomer() {
        return Response.ok().entity(String.format("<html><body><h1>Welcome to Restbucks</h1><p>You can place your order by POSTing to %s</p></body></html>", uriInfo.getAbsolutePath() + "order")).build();
    }

}
