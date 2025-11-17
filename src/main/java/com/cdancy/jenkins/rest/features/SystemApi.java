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

package com.cdancy.jenkins.rest.features;

import static com.cdancy.jenkins.rest.parsers.ResponseResult.of;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofSystemInfo;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofVoid;

import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.cdancy.jenkins.rest.parsers.ResponseResult;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
public interface SystemApi {

    /**
     * Raw HEAD request to get Jenkins system info headers.
     */
    @HEAD
    @Path("/")
    Response systemInfoRaw();

    /**
     * Functional helper that parses Jenkins system info headers into a SystemInfo domain object.
     */
    default ResponseResult<SystemInfo> systemInfo() {
        Response response = systemInfoRaw();
        return ofSystemInfo(response);
    }

    /**
     * Raw POST request to quiet down the Jenkins system.
     */
    @POST
    @Path("quietDown")
    @Consumes(MediaType.TEXT_HTML)
    Response quietDownRaw();

    /**
     * Helper for quietDown that returns a typed result.
     */
    default ResponseResult<Void> quietDown() {
        return ofVoid(quietDownRaw());
    }

    /**
     * Raw POST request to cancel the quiet down mode.
     */
    @POST
    @Path("/cancelQuietDown")
    @Consumes(MediaType.TEXT_HTML)
    Response cancelQuietDownRaw();

    /**
     * Helper for cancelQuietDown that returns a typed result.
     */
    default ResponseResult<Void> cancelQuietDown() {
        return ofVoid(cancelQuietDownRaw());
    }
}
