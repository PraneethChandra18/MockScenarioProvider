package org.example.metrics.healthchecks;

import com.codahale.metrics.health.HealthCheck;

public class ServerHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception{
        return Result.healthy();
    }
}
