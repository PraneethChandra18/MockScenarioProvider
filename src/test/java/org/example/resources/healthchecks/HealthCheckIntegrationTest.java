package org.example.resources.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.example.metrics.healthchecks.ServerHealthCheck;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthCheckIntegrationTest {

    @Test
    public void HealthCheckTest(){
        HealthCheckRegistry healthCheckRegistry=new HealthCheckRegistry();

        healthCheckRegistry.register("server", new ServerHealthCheck());
        assertThat(healthCheckRegistry.getNames().size()).isEqualTo(1);

        Map<String, HealthCheck.Result> results=healthCheckRegistry.runHealthChecks();

        assertFalse(results.isEmpty());

        results.forEach((k,v)->assertTrue(v.isHealthy()));

        healthCheckRegistry.unregister("server");

        assertThat(healthCheckRegistry.getNames().size()).isEqualTo(0);
    }
}
