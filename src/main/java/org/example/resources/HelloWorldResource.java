package org.example.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/helloWorld")
@Api(value = "/helloWorld", description = "working fine!")
@Produces(MediaType.TEXT_PLAIN)
public class HelloWorldResource {


    public HelloWorldResource() { }

    @GET
    @UnitOfWork
    @ApiOperation(
            value = "Hello World!",
            notes = "Returns Hello World")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Server is working fine")})
    public String helloWorld() {
        return "Hello World";
    }
}
