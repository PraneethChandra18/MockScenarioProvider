package org.example.metrics.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import org.example.api.Data;

/*
Implementing simple data health check which checks the data as produced
when running the application file(server file) is as expected or not
 */

public class DataHealthCheck extends HealthCheck {

    private final Data data;
    public DataHealthCheck(Data data){
        this.data=data;
    }

    @Override
    protected Result check(){
        final Data data=new Data();
        data.name="";
        data.description="";
        if(!(this.data.getName().equals(data.getName()) && this.data.description.equals(data.description)))
            return Result.unhealthy("Health Check Failed");
        return Result.healthy();
    }
}
