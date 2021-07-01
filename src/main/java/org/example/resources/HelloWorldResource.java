package org.example.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/helloWorld")
@Api(value = "/helloWorld", description = "working fine!")
@Produces(MediaType.TEXT_PLAIN)
public class HelloWorldResource {

    private MetricRegistry service;

    public HelloWorldResource(MetricRegistry service) {
        this.service=service;
    }

    @GET
    @UnitOfWork
    @ApiOperation(
            value = "Hello World!",
            notes = "Returns Hello World")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Server is working fine")})
    public String helloWorld() {
        Timer timer=service.timer("hello-world");
        try(Timer.Context t=timer.time()) {
            return "Hello World";
        }
    }
}
