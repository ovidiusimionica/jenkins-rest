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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

//{
//    "_class" : "hudson.model.FreeStyleBuild",
//    "actions" : [ {
//    "_class" : "hudson.model.CauseAction",
//    "causes" : [ {
//    "_class" : "hudson.model.Cause$UserIdCause",
//    "shortDescription" : "Started by user admin",
//    "userId" : "admin",
//    "userName" : "admin"
//    } ]
//    }, {
//    "_class" : "jenkins.model.InterruptedBuildAction"
//    } ],
//    "artifacts" : [ ],
//    "building" : false,
//    "description" : null,
//    "displayName" : "#1",
//    "duration" : 1474,
//    "estimatedDuration" : -1,
//    "executor" : null,
//    "fullDisplayName" : "FreeStyleSleep #1",
//    "id" : "1",
//    "inProgress" : false,
//    "keepLog" : false,
//    "number" : 1,
//    "queueId" : 14,
//    "result" : "ABORTED",
//    "timestamp" : 1762959092218,
//    "url" : "http://127.0.0.1:8080/job/FreeStyleSleep/1/",
//    "builtOn" : "",
//    "changeSet" : {
//    "_class" : "hudson.scm.EmptyChangeLogSet",
//    "items" : [ ],
//    "kind" : null
//    },
//    "culprits" : [ ]
//    }

@JsonIgnoreProperties({"_class", "executor", "nextBuild", "previousBuild"})
public final class BuildInfo {

    private final List<Artifact> artifacts;
    private final List<Action> actions;
    private final boolean inProgress;
    private final boolean building;
    private final String description;
    private final String displayName;
    private final long duration;
    private final long estimatedDuration;
    private final String fullDisplayName;
    private final String id;
    private final boolean keepLog;
    private final int number;
    private final int queueId;
    private final String result;
    private final long timestamp;
    private final String url;
    private final ChangeSetList changeSet;
    private final List<ChangeSetList> changeSets;
    private final String builtOn;
    private final List<Culprit> culprits;

    @JsonCreator
    public BuildInfo(
        @JsonProperty("artifacts") List<Artifact> artifacts,
        @JsonProperty("actions") List<Action> actions,
        @JsonProperty("inProgress") boolean inProgress,
        @JsonProperty("building") boolean building,
        @JsonProperty("description") String description,
        @JsonProperty("displayName") String displayName,
        @JsonProperty("duration") long duration,
        @JsonProperty("estimatedDuration") long estimatedDuration,
        @JsonProperty("fullDisplayName") String fullDisplayName,
        @JsonProperty("id") String id,
        @JsonProperty("keepLog") boolean keepLog,
        @JsonProperty("number") int number,
        @JsonProperty("queueId") int queueId,
        @JsonProperty("result") String result,
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("url") String url,
        @JsonProperty("changeSet") ChangeSetList changeSet,
        @JsonProperty("changeSets") List<ChangeSetList> changeSets,
        @JsonProperty("builtOn") String builtOn,
        @JsonProperty("culprits") List<Culprit> culprits
    ) {
        this.artifacts = artifacts != null ? List.copyOf(artifacts) : Collections.emptyList();
        this.actions = actions != null ? List.copyOf(actions) : Collections.emptyList();
        this.inProgress = inProgress;
        this.building = building;
        this.description = description;
        this.displayName = displayName;
        this.duration = duration;
        this.estimatedDuration = estimatedDuration;
        this.fullDisplayName = fullDisplayName;
        this.id = id;
        this.keepLog = keepLog;
        this.number = number;
        this.queueId = queueId;
        this.result = result;
        this.timestamp = timestamp;
        this.url = url;
        this.changeSet = changeSet;
        this.changeSets = changeSets != null ? List.copyOf(changeSets) : Collections.emptyList();;
        this.builtOn = builtOn;
        this.culprits = culprits != null ? List.copyOf(culprits) : Collections.emptyList();
    }

    public List<Artifact> getArtifacts() { return artifacts; }
    public List<Action> getActions() { return actions; }
    public boolean isInProgress() { return inProgress; }
    public boolean isBuilding() { return building; }
    public String getDescription() { return description; }
    public String getDisplayName() { return displayName; }
    public long getDuration() { return duration; }
    public long getEstimatedDuration() { return estimatedDuration; }
    public String getFullDisplayName() { return fullDisplayName; }
    public String getId() { return id; }
    public boolean isKeepLog() { return keepLog; }
    public int getNumber() { return number; }
    public int getQueueId() { return queueId; }
    public String getResult() { return result; }
    public long getTimestamp() { return timestamp; }
    public String getUrl() { return url; }
    public ChangeSetList getChangeSet() { return changeSet; }
    public List<ChangeSetList> getChangeSets() { return changeSets; }
    public String getBuiltOn() { return builtOn; }
    public List<Culprit> getCulprits() { return culprits; }
}
