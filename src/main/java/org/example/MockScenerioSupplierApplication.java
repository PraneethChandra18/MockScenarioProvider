package org.example;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.example.api.Data;
import org.example.metrics.healthchecks.DataHealthCheck;
import org.example.resources.HelloWorldResource;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.example.resources.MockScenarioResource;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class MockScenerioSupplierApplication extends Application<MockScenerioSupplierConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MockScenerioSupplierApplication().run(args);
    }

    @Override
    public String getName() {
        return "MockScenerioSupplier";
    }

    @Override
    public void initialize(final Bootstrap<MockScenerioSupplierConfiguration> bootstrap) {
        // TODO: application initialization

        bootstrap.addBundle(
                new SwaggerBundle<MockScenerioSupplierConfiguration>() {
                    @Override
                    protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
                            MockScenerioSupplierConfiguration configuration) {
                        return configuration.getSwagger();
                    }
                });
    }

    @Override
    public void run(final MockScenerioSupplierConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application

        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // An Object of Data Class for Health Checks
        Data data=new Data();
        data.name="";
        data.description="";


        // Registering the Health Checks
        final DataHealthCheck dataHealthCheck=new DataHealthCheck(data);
        environment.healthChecks().register("data",dataHealthCheck);

        //Registering the metrics
        MetricRegistry metrics=environment.metrics();

        //Registering the Resource Classes
        environment.jersey().register(new HelloWorldResource(metrics));
        environment.jersey().register(new MockScenarioResource(metrics));

        //For reporting the logs on the console every t seconds (creates a problem while testing)
        //ConsoleReporter.forRegistry(metrics).build().start(1, TimeUnit.SECONDS);
    }

}
