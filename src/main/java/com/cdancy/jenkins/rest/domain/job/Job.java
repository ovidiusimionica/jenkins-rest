package com.cdancy.jenkins.rest.domain.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final record Job(String clazz, String name, String url, String color) {

    @JsonCreator
    public Job(
        @JsonProperty("_class") String clazz,
        @JsonProperty("name") String name,
        @JsonProperty("url") String url,
        @JsonProperty("color") String color
    ) {
        this.clazz = clazz;
        this.name = name;
        this.url = url;
        this.color = color;
    }
}
