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

//import com.cdancy.jenkins.rest.domain.common.RequestStatus;
//import java.util.List;
//
//import javax.inject.Named;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.FormParam;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.PathParam;
//import jakarta.ws.rs.core.MediaType;
//
//import org.jclouds.rest.annotations.Fallback;
//import org.jclouds.rest.annotations.RequestFilters;
//import org.jclouds.rest.annotations.SelectJson;
//
//import com.cdancy.jenkins.rest.domain.queue.QueueItem;
//import com.cdancy.jenkins.rest.fallbacks.JenkinsFallbacks;
//import com.cdancy.jenkins.rest.filters.JenkinsAuthenticationFilter;
//import com.cdancy.jenkins.rest.parsers.RequestStatusParser;
//import org.jclouds.rest.annotations.ResponseParser;
//
//@RequestFilters(JenkinsAuthenticationFilter.class)
//@Consumes(MediaType.APPLICATION_JSON)
//@Path("/queue")
//public interface QueueApi {
//
//    @Named("queue:queue")
//    @Path("/api/json")
//    @SelectJson("items")
//    @GET
//    List<QueueItem> queue();
//
//    /**
//     * Get a specific queue item.
//     *
//     * Queue items are builds that have been scheduled to run, but are waiting for a slot.
//     * You can poll the queueItem that corresponds to a build to detect whether the build is still pending or is executing.
//     * @param queueId The queue id value as returned by the JobsApi build or buildWithParameters methods.
//     * @return The queue item corresponding to the queue id.
//     */
//    @Named("queue:item")
//    @Path("/item/{queueId}/api/json")
//    @GET
//    QueueItem queueItem(@PathParam("queueId") long queueId);
//
//    /**
//     * Cancel a queue item before it gets built.
//     *
//     * @param id The queue id value of the queue item to cancel.
//     *           This is the value is returned by the JobsApi build or buildWithParameters methods.
//     * @return Always returns true due to JENKINS-21311.
//     */
//    @Named("queue:cancel")
//    @Path("/cancelItem")
//    @Fallback(JenkinsFallbacks.JENKINS_21311.class)
//    @ResponseParser(RequestStatusParser.class)
//    @POST
//    RequestStatus cancel(@FormParam("id") long id);
//}


import static com.cdancy.jenkins.rest.parsers.ResponseResult.of;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofVoid;

import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.parsers.ResponseResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/queue")
@Consumes(MediaType.APPLICATION_JSON)
public interface QueueApi
{

    // Wrapper class to match the JSON structure returned by Jenkins
    @JsonIgnoreProperties(ignoreUnknown = true)
    class QueueItemsWrapper
    {
        public List<QueueItem> items;
    }

    @GET
    @Path("/api/json")
    Response queueRaw();

    default ResponseResult<List<QueueItem>> queue()
    {
        Response response = queueRaw();
        ResponseResult<QueueItemsWrapper> wrapperResult = ResponseResult.of(response, QueueItemsWrapper.class);
        List<QueueItem> items = wrapperResult.getEntity() != null ? wrapperResult.getEntity().items : null;
        return ResponseResult.of(response, items, wrapperResult.getError());
    }

    @GET
    @Path("/item/{queueId}/api/json")
    Response queueItemRaw(@PathParam("queueId") long queueId);

    default ResponseResult<QueueItem> queueItem(long queueId)
    {
        Response response = queueItemRaw(queueId);
        return of(response, QueueItem.class);
    }

    @POST
    @Path("/cancelItem")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response cancelRaw(@FormParam("id") long id);

    default ResponseResult<Void> cancel(long id)
    {
        return ofVoid(cancelRaw(id));
    }
}

