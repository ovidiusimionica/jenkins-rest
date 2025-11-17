package com.cdancy.jenkins.rest.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Property {

    private final String clazz;

    @JsonCreator
    public Property(@JsonProperty("_class") String clazz) {
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }
}
