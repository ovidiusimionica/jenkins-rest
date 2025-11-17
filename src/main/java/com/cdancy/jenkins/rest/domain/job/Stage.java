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

public final class Stage {

    private final String id;
    private final String name;
    private final String status;
    private final long startTimeMillis;
    private final long endTimeMillis;
    private final long pauseDurationMillis;
    private final long durationMillis;

    @JsonCreator
    public Stage(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("status") String status,
        @JsonProperty("startTimeMillis") long startTimeMillis,
        @JsonProperty("endTimeMillis") long endTimeMillis,
        @JsonProperty("pauseDurationMillis") long pauseDurationMillis,
        @JsonProperty("durationMillis") long durationMillis
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.pauseDurationMillis = pauseDurationMillis;
        this.durationMillis = durationMillis;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public long getPauseDurationMillis() {
        return pauseDurationMillis;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
