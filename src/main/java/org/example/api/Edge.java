package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Edge {

    public String source;
    public List<String> target;

    @JsonProperty
    public String getSource(){
        return source;
    }

    @JsonProperty
    public List<String> getTarget(){
        return target;
    }

}
