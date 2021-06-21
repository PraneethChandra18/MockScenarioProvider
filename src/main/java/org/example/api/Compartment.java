package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Compartment {

    public String id;
    public String name;
    public List<Resources> resources;
    public List<Compartment> children;

    @JsonProperty
    public String getId(){
        return id;
    }

    @JsonProperty
    public String getName(){
        return name;
    }

    @JsonProperty
    public List<Resources> getResources(){
        return resources;
    }

    @JsonProperty
    public List<Compartment> getChildren(){
        return children;
    }
}
