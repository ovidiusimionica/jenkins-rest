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
package com.cdancy.jenkins.rest.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"_class", "property"})
public final class User {

    private final String absoluteUrl;
    private final String description;
    private final String fullName;
    private final String id;

    @JsonCreator
    public User(
        @JsonProperty("absoluteUrl") String absoluteUrl,
        @JsonProperty("description") String description,
        @JsonProperty("fullName") String fullName,
        @JsonProperty("id") String id
    ) {
        this.absoluteUrl = absoluteUrl;
        this.description = description;
        this.fullName = fullName;
        this.id = id;
    }

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }
}
