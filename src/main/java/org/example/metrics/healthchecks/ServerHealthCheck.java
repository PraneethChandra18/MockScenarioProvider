package org.example.metrics.healthchecks;

import com.codahale.metrics.health.HealthCheck;


/*
Would test this health check for further ensuring
 */

public class ServerHealthCheck extends HealthCheck {
    @Override
    protected Result check(){
        return Result.healthy();
    }
}
