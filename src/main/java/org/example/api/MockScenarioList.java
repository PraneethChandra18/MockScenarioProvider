package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MockScenarioList {

    public int count;
    public List<Detail> mockScenarioList;

    @JsonProperty
    public List<Detail> getMockScenarioList(){
        return mockScenarioList;
    }
}
