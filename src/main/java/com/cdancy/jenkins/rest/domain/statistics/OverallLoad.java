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

package com.cdancy.jenkins.rest.domain.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties("_class")
public final class OverallLoad {

    private final Map<String, String> availableExecutors;
    private final Map<String, String> busyExecutors;
    private final Map<String, String> connectingExecutors;
    private final Map<String, String> definedExecutors;
    private final Map<String, String> idleExecutors;
    private final Map<String, String> onlineExecutors;
    private final Map<String, String> queueLength;
    private final Map<String, String> totalExecutors;
    private final Map<String, String> totalQueueLength;

    @JsonCreator
    public OverallLoad(
        @JsonProperty("availableExecutors") Map<String, String> availableExecutors,
        @JsonProperty("busyExecutors") Map<String, String> busyExecutors,
        @JsonProperty("connectingExecutors") Map<String, String> connectingExecutors,
        @JsonProperty("definedExecutors") Map<String, String> definedExecutors,
        @JsonProperty("idleExecutors") Map<String, String> idleExecutors,
        @JsonProperty("onlineExecutors") Map<String, String> onlineExecutors,
        @JsonProperty("queueLength") Map<String, String> queueLength,
        @JsonProperty("totalExecutors") Map<String, String> totalExecutors,
        @JsonProperty("totalQueueLength") Map<String, String> totalQueueLength
    ) {
        this.availableExecutors = availableExecutors;
        this.busyExecutors = busyExecutors;
        this.connectingExecutors = connectingExecutors;
        this.definedExecutors = definedExecutors;
        this.idleExecutors = idleExecutors;
        this.onlineExecutors = onlineExecutors;
        this.queueLength = queueLength;
        this.totalExecutors = totalExecutors;
        this.totalQueueLength = totalQueueLength;
    }

    public Map<String, String> getAvailableExecutors() {
        return availableExecutors;
    }

    public Map<String, String> getBusyExecutors() {
        return busyExecutors;
    }

    public Map<String, String> getConnectingExecutors() {
        return connectingExecutors;
    }

    public Map<String, String> getDefinedExecutors() {
        return definedExecutors;
    }

    public Map<String, String> getIdleExecutors() {
        return idleExecutors;
    }

    public Map<String, String> getOnlineExecutors() {
        return onlineExecutors;
    }

    public Map<String, String> getQueueLength() {
        return queueLength;
    }

    public Map<String, String> getTotalExecutors() {
        return totalExecutors;
    }

    public Map<String, String> getTotalQueueLength() {
        return totalQueueLength;
    }
}
