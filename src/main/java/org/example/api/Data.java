package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/*
Generating the data model as required
 */

public class Data {
    public String name;
    public List<Compartment> nodes;
    public List<Edge> edges;
    public String description;

    @JsonProperty
    public String getName(){
        return name;
    }

    @JsonProperty
    public List<Compartment> getNodes(){
        return nodes;
    }

    @JsonProperty
    public List<Edge> getEdges(){
        return edges;
    }

    @JsonProperty
    public String getDescription(){
        return description;
    }

}
