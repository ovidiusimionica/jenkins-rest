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

public final class ChangeSet {

    private final List<String> affectedPaths;
    private final String commitId;
    private final long timestamp;
    private final Culprit author;
    private final String authorEmail;
    private final String comment;

    @JsonCreator
    public ChangeSet(
        @JsonProperty("affectedPaths") List<String> affectedPaths,
        @JsonProperty("commitId") String commitId,
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("author") Culprit author,
        @JsonProperty("authorEmail") String authorEmail,
        @JsonProperty("comment") String comment
    ) {
        this.affectedPaths = affectedPaths != null ? List.copyOf(affectedPaths) : Collections.emptyList();
        this.commitId = commitId;
        this.timestamp = timestamp;
        this.author = author;
        this.authorEmail = authorEmail;
        this.comment = comment;
    }

    public List<String> getAffectedPaths() {
        return affectedPaths;
    }

    public String getCommitId() {
        return commitId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Culprit getAuthor() {
        return author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getComment() {
        return comment;
    }
}
