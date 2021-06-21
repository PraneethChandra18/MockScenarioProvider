package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Detail {
    public String name;
    public String id;
    public String description;

    @JsonProperty
    public String getName(){
        return name;
    }

    @JsonProperty
    public String getId(){
        return id;
    }

    @JsonProperty
    public String getDescription(){
        return description;
    }

}
