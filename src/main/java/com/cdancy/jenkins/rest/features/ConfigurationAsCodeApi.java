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

import com.cdancy.jenkins.rest.parsers.ResponseResult;

import static com.cdancy.jenkins.rest.parsers.ResponseResult.of;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofVoid;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Modernized Configuration-as-Code API using JAX-RS ResponseResult pattern.
 */
@Path("/configuration-as-code")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ConfigurationAsCodeApi {

    // -----------------------
    // RAW: check configuration
    // -----------------------
    @POST
    @Path("/check")
    @Consumes(MediaType.TEXT_PLAIN)
    Response checkRaw(String cascYml);

    default ResponseResult<Void> check(String cascYml) {
        return ofVoid(checkRaw(cascYml));
    }

    // -----------------------
    // RAW: apply configuration
    // -----------------------
    @POST
    @Path("/apply")
    @Consumes(MediaType.TEXT_PLAIN)
    Response applyRaw(String cascYml);

    default ResponseResult<Void> apply(String cascYml) {
        return ofVoid(applyRaw(cascYml));
    }
}
