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

public final class Cause {

    private final String clazz;
    private final String shortDescription;
    private final String userId;
    private final String userName;

    @JsonCreator
    public Cause(
        @JsonProperty("_class") String clazz,
        @JsonProperty("shortDescription") String shortDescription,
        @JsonProperty("userId") String userId,
        @JsonProperty("userName") String userName
    ) {
        this.clazz = clazz;
        this.shortDescription = shortDescription;
        this.userId = userId;
        this.userName = userName;
    }

    @JsonProperty("_class")
    public String getClazz() {
        return clazz;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
