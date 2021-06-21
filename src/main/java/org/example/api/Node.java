package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Node {

    public List<Compartment> nodes;

    @JsonProperty
    public List<Compartment> getNodes(){
        return nodes;
    }
}
