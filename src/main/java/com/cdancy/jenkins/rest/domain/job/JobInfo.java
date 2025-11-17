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

import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class JobInfo {

    private final String description;
    private final String displayName;
    private final String displayNameOrNull;
    private final String name;
    private final String url;
    private final boolean buildable;
    private final List<BuildInfo> builds;
    private final String color;
    private final BuildInfo firstBuild;
    private final boolean inQueue;
    private final boolean keepDependencies;
    private final BuildInfo lastBuild;
    private final BuildInfo lastCompleteBuild;
    private final BuildInfo lastFailedBuild;
    private final BuildInfo lastStableBuild;
    private final BuildInfo lastSuccessfulBuild;
    private final BuildInfo lastUnstableBuild;
    private final BuildInfo lastUnsuccessfulBuild;
    private final int nextBuildNumber;
    private final QueueItem queueItem;
    private final boolean concurrentBuild;

    @JsonCreator
    public JobInfo(
        @JsonProperty("description") String description,
        @JsonProperty("displayName") String displayName,
        @JsonProperty("displayNameOrNull") String displayNameOrNull,
        @JsonProperty("name") String name,
        @JsonProperty("url") String url,
        @JsonProperty("buildable") boolean buildable,
        @JsonProperty("builds") List<BuildInfo> builds,
        @JsonProperty("color") String color,
        @JsonProperty("firstBuild") BuildInfo firstBuild,
        @JsonProperty("inQueue") boolean inQueue,
        @JsonProperty("keepDependencies") boolean keepDependencies,
        @JsonProperty("lastBuild") BuildInfo lastBuild,
        @JsonProperty("lastCompleteBuild") BuildInfo lastCompleteBuild,
        @JsonProperty("lastFailedBuild") BuildInfo lastFailedBuild,
        @JsonProperty("lastStableBuild") BuildInfo lastStableBuild,
        @JsonProperty("lastSuccessfulBuild") BuildInfo lastSuccessfulBuild,
        @JsonProperty("lastUnstableBuild") BuildInfo lastUnstableBuild,
        @JsonProperty("lastUnsuccessfulBuild") BuildInfo lastUnsuccessfulBuild,
        @JsonProperty("nextBuildNumber") int nextBuildNumber,
        @JsonProperty("queueItem") QueueItem queueItem,
        @JsonProperty("concurrentBuild") boolean concurrentBuild
    ) {
        this.description = description;
        this.displayName = displayName;
        this.displayNameOrNull = displayNameOrNull;
        this.name = name;
        this.url = url;
        this.buildable = buildable;
        this.builds = builds != null ? List.copyOf(builds) : Collections.emptyList();
        this.color = color;
        this.firstBuild = firstBuild;
        this.inQueue = inQueue;
        this.keepDependencies = keepDependencies;
        this.lastBuild = lastBuild;
        this.lastCompleteBuild = lastCompleteBuild;
        this.lastFailedBuild = lastFailedBuild;
        this.lastStableBuild = lastStableBuild;
        this.lastSuccessfulBuild = lastSuccessfulBuild;
        this.lastUnstableBuild = lastUnstableBuild;
        this.lastUnsuccessfulBuild = lastUnsuccessfulBuild;
        this.nextBuildNumber = nextBuildNumber;
        this.queueItem = queueItem;
        this.concurrentBuild = concurrentBuild;
    }

    public String getDescription() { return description; }
    public String getDisplayName() { return displayName; }
    public String getDisplayNameOrNull() { return displayNameOrNull; }
    public String getName() { return name; }
    public String getUrl() { return url; }
    public boolean isBuildable() { return buildable; }
    public List<BuildInfo> getBuilds() { return builds; }
    public String getColor() { return color; }
    public BuildInfo getFirstBuild() { return firstBuild; }
    public boolean isInQueue() { return inQueue; }
    public boolean isKeepDependencies() { return keepDependencies; }
    public BuildInfo getLastBuild() { return lastBuild; }
    public BuildInfo getLastCompleteBuild() { return lastCompleteBuild; }
    public BuildInfo getLastFailedBuild() { return lastFailedBuild; }
    public BuildInfo getLastStableBuild() { return lastStableBuild; }
    public BuildInfo getLastSuccessfulBuild() { return lastSuccessfulBuild; }
    public BuildInfo getLastUnstableBuild() { return lastUnstableBuild; }
    public BuildInfo getLastUnsuccessfulBuild() { return lastUnsuccessfulBuild; }
    public int getNextBuildNumber() { return nextBuildNumber; }
    public QueueItem getQueueItem() { return queueItem; }
    public boolean isConcurrentBuild() { return concurrentBuild; }
}
