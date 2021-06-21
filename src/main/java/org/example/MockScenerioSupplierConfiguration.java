package org.example;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;
import javax.validation.constraints.*;
import javax.validation.Valid;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class MockScenerioSupplierConfiguration extends Configuration {
    // TODO: implement service configuration

    @Valid @NotNull
    private final SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }
}
