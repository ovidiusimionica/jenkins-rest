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

package com.cdancy.jenkins.rest.domain.crumb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Crumb {

    @JsonProperty("crumbRequestField")
    private String crumbRequestField;

    @JsonProperty("crumb")
    private String crumb;

    /** Filled from Set-Cookie header */
    private String sessionIdCookie;

    public Crumb() {}

    public Crumb(String crumbRequestField, String crumb, String sessionIdCookie) {
        this.crumbRequestField = crumbRequestField;
        this.crumb = crumb;
        this.sessionIdCookie = sessionIdCookie;
    }

    public String getCrumbRequestField() { return crumbRequestField; }
    public String getCrumb() { return crumb; }
    public String getSessionIdCookie() { return sessionIdCookie; }

    public void setCrumbRequestField(String v) { this.crumbRequestField = v; }
    public void setCrumb(String v) { this.crumb = v; }
    public void setSessionIdCookie(String v) { this.sessionIdCookie = v; }

    @Override
    public String toString() {
        return "Crumb{" +
            "crumbRequestField='" + crumbRequestField + '\'' +
            ", crumb='" + crumb + '\'' +
            ", sessionIdCookie='" + sessionIdCookie + '\'' +
            '}';
    }
}
