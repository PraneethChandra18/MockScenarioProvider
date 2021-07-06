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

/*
* Sameple Hello World Resource for Hello World api to the backend server
*/

@Path("/helloWorld")
@Api(value = "/helloWorld", description = "working fine!")
@Produces(MediaType.TEXT_PLAIN)      //Keeping the output format as simple text (string)
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
        //Implement the timer to get the metrics related to the time taken by the helloWorld api
        Timer timer=service.timer("hello-world");
        try(Timer.Context t=timer.time()) {
            return "Hello World";
        }
    }
}
