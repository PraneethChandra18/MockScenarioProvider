package org.example.metrics.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import org.example.api.Data;


public class DataHealthCheck extends HealthCheck {

    private final Data data;
    public DataHealthCheck(Data data){
        this.data=data;
    }

    @Override
    protected Result check() throws Exception{
        final Data data=new Data();
        data.name="";
        data.description="";
        if(!(this.data.getName().equals(data.getName()) && this.data.description.equals(data.description)))
            return Result.unhealthy("Health Check Failed");
        return Result.healthy();
    }
}
