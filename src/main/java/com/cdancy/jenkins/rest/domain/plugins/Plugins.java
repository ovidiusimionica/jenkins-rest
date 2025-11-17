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

package com.cdancy.jenkins.rest.domain.plugins;

import com.cdancy.jenkins.rest.domain.common.ErrorsHolder;
import com.cdancy.jenkins.rest.domain.common.GenericError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Plugins implements ErrorsHolder
{

    private final String clazz;
    private final List<Plugin> plugins;
    private final List<GenericError> errors;

    @JsonCreator
    public Plugins(
        @JsonProperty("_class") String clazz,
        @JsonProperty("plugins") List<Plugin> plugins,
        @JsonProperty("errors") List<GenericError> errors
    ) {
        this.clazz = clazz;
        this.plugins = plugins != null ? List.copyOf(plugins) : Collections.emptyList();
        this.errors = errors != null ? List.copyOf(errors) : Collections.emptyList();
    }

    public String getClazz() {
        return clazz;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public List<GenericError> errors() {
        return errors;
    }
}
