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

package com.cdancy.jenkins.rest.domain.system;

import com.cdancy.jenkins.rest.domain.common.ErrorsHolder;
import com.cdancy.jenkins.rest.domain.common.GenericError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class SystemInfo implements ErrorsHolder {

    private final String hudsonVersion;
    private final String jenkinsVersion;
    private final String jenkinsSession;
    private final String instanceIdentity;
    private final String sshEndpoint;
    private final String server;
    private final List<GenericError> errors;

    @JsonCreator
    public SystemInfo(
        @JsonProperty("hudsonVersion") String hudsonVersion,
        @JsonProperty("jenkinsVersion") String jenkinsVersion,
        @JsonProperty("jenkinsSession") String jenkinsSession,
        @JsonProperty("instanceIdentity") String instanceIdentity,
        @JsonProperty("sshEndpoint") String sshEndpoint,
        @JsonProperty("server") String server,
        @JsonProperty("errors") List<GenericError> errors
    ) {
        this.hudsonVersion = hudsonVersion;
        this.jenkinsVersion = jenkinsVersion;
        this.jenkinsSession = jenkinsSession;
        this.instanceIdentity = instanceIdentity;
        this.sshEndpoint = sshEndpoint;
        this.server = server;
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public String getHudsonVersion() {
        return hudsonVersion;
    }

    public String getJenkinsVersion() {
        return jenkinsVersion;
    }

    public String getJenkinsSession() {
        return jenkinsSession;
    }

    public String getInstanceIdentity() {
        return instanceIdentity;
    }

    public String getSshEndpoint() {
        return sshEndpoint;
    }

    public String getServer() {
        return server;
    }

    public List<GenericError> errors() {
        return errors;
    }
}
