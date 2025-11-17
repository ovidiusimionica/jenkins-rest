/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.jenkins.rest.domain.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class Action {

    private final List<Cause> causes;
    private final List<Parameter> parameters;
    private final String text;
    private final String iconPath;
    private final String clazz;

    @JsonCreator
    public Action(
        @JsonProperty("causes") List<Cause> causes,
        @JsonProperty("parameters") List<Parameter> parameters,
        @JsonProperty("text") String text,
        @JsonProperty("iconPath") String iconPath,
        @JsonProperty("_class") String clazz
    ) {
        this.causes = causes != null ? List.copyOf(causes) : Collections.emptyList();
        this.parameters = parameters != null ? List.copyOf(parameters) : Collections.emptyList();
        this.text = text;
        this.iconPath = iconPath;
        this.clazz = clazz;
    }

    public List<Cause> getCauses() {
        return causes;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getText() {
        return text;
    }

    public String getIconPath() {
        return iconPath;
    }

    @JsonProperty("_class")
    public String getClazz() {
        return clazz;
    }
}
