package com.ddlab.rnd.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import tools.jackson.databind.ObjectMapper;

@Data
@AllArgsConstructor
public class HostedRepo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String shortDescription;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        String toJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        return toJson;
    }

}
