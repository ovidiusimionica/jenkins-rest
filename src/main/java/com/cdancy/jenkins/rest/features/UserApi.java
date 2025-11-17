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

import com.cdancy.jenkins.rest.domain.user.ApiToken;
import com.cdancy.jenkins.rest.domain.user.User;
import com.cdancy.jenkins.rest.parsers.ResponseResult;

import static com.cdancy.jenkins.rest.parsers.ResponseResult.of;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofVoid;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserApi {

    String CURRENT_USER = "{user}";

    // -----------------------
    // RAW: get user
    // -----------------------
    @GET
    @Path("/{user}/api/json")
    Response getRaw(@PathParam("user") String user);

    default ResponseResult<User> get() {
        return of(getRaw(CURRENT_USER), User.class);
    }

    // -----------------------
    // RAW: generate new token (accepts full form-encoded payload)
    // -----------------------
    @POST
    @Path("/{user}/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Response generateNewTokenRaw(@PathParam("user") String user, String payload);

    /**
     * Helper that replicates @Payload("newTokenName={tokenName}") behaviour.
     */
    default ResponseResult<ApiToken> generateNewToken(String tokenName) {
        String encoded = URLEncoder.encode(tokenName == null ? "" : tokenName, StandardCharsets.UTF_8);
        String payload = "newTokenName=" + encoded;
        return of(generateNewTokenRaw(CURRENT_USER, payload), ApiToken.class);
    }

    // -----------------------
    // RAW: revoke token (accepts full form-encoded payload)
    // -----------------------
    @POST
    @Path("/{user}/descriptorByName/jenkins.security.ApiTokenProperty/revoke")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Response revokeRaw(@PathParam("user") String user, String payload);

    /**
     * Helper that replicates @Payload("tokenUuid={tokenUuid}") behaviour.
     */
    default ResponseResult<Void> revoke(String tokenUuid) {
        String encoded = URLEncoder.encode(tokenUuid == null ? "" : tokenUuid, StandardCharsets.UTF_8);
        String payload = "tokenUuid=" + encoded;
        return ofVoid(revokeRaw(CURRENT_USER, payload));
    }
}
