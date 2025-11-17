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

package com.cdancy.jenkins.rest.domain.queue;

import com.cdancy.jenkins.rest.domain.common.FromStringToMapDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class QueueItem
{

    private final boolean blocked;
    private final boolean buildable;
    private final int id;
    private final long inQueueSince;
    private final Map<String, String> params;
    private final boolean stuck;
    private final Task task;
    private final String url;
    private final String why;
    private final long buildableStartMilliseconds;
    private final boolean cancelled;
    private final Executable executable;
    private final Long timestamp;

    @JsonCreator
    public QueueItem(
        @JsonProperty("blocked") boolean blocked,
        @JsonProperty("buildable") boolean buildable,
        @JsonProperty("id") int id,
        @JsonProperty("inQueueSince") long inQueueSince,
        @JsonProperty("params") @JsonDeserialize(using = FromStringToMapDeserializer.class)
        Map<String, String> params,
        @JsonProperty("stuck") boolean stuck,
        @JsonProperty("task") Task task,
        @JsonProperty("url") String url,
        @JsonProperty("why") String why,
        @JsonProperty("buildableStartMilliseconds") long buildableStartMilliseconds,
        @JsonProperty("cancelled") boolean cancelled,
        @JsonProperty("executable") Executable executable,
        @JsonProperty("timestamp") Long timestamp
    )
    {
        this.blocked = blocked;
        this.buildable = buildable;
        this.id = id;
        this.inQueueSince = inQueueSince;
        this.params = params != null ? Collections.unmodifiableMap(new HashMap<>(params)) : Collections.emptyMap();
        this.stuck = stuck;
        this.task = task;
        this.url = url;
        this.why = why;
        this.buildableStartMilliseconds = buildableStartMilliseconds;
        this.cancelled = cancelled;
        this.executable = executable;
        this.timestamp = timestamp;
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public boolean isBuildable()
    {
        return buildable;
    }

    public int getId()
    {
        return id;
    }

    public long getInQueueSince()
    {
        return inQueueSince;
    }

    public Map<String, String> getParams()
    {
        return params;
    }

    public boolean isStuck()
    {
        return stuck;
    }

    public Task getTask()
    {
        return task;
    }

    public String getUrl()
    {
        return url;
    }

    public String getWhy()
    {
        return why;
    }

    public long getBuildableStartMilliseconds()
    {
        return buildableStartMilliseconds;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public Executable getExecutable()
    {
        return executable;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }
}
