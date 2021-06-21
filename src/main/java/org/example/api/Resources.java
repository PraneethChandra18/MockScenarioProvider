package org.example.api;

import java.util.List;

public class Resources {
    public String resourceType;
    public List<String> items;

    public String getResourceType(){
        return resourceType;
    }

    public List<String> getItems(){
        return items;
    }
}
