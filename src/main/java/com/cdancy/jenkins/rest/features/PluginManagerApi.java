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
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofVoid;

import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import com.cdancy.jenkins.rest.parsers.ResponseResult;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/pluginManager")
@Consumes(MediaType.APPLICATION_JSON)
public interface PluginManagerApi {

    @GET
    @Path("/api/json")
    Response pluginsRaw(@QueryParam("depth") Integer depth,
                                    @QueryParam("tree") String tree);


    default ResponseResult<Plugins> plugins(Integer depth, String tree) {
        Response response = pluginsRaw(depth, tree);
        return of(response, Plugins.class);
    }

    @POST
    @Path("/installNecessaryPlugins")
    @Produces(MediaType.APPLICATION_XML)
    Response installNecessaryPlugins(String payload);

    /**
     * Default helper to install a plugin with functional-style response.
     */
    default ResponseResult<Void> installNecessaryPluginsById(String pluginID) {
        String xmlPayload = "<jenkins><install plugin=\"" + pluginID + "\"/></jenkins>";
        return ofVoid(installNecessaryPlugins(xmlPayload));
    }
}
