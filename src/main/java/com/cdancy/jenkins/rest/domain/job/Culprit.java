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

public final class Culprit {

    private final String absoluteUrl;
    private final String fullName;

    @JsonCreator
    public Culprit(
        @JsonProperty("absoluteUrl") String absoluteUrl,
        @JsonProperty("fullName") String fullName
    ) {
        this.absoluteUrl = absoluteUrl;
        this.fullName = fullName;
    }

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public String getFullName() {
        return fullName;
    }
}
