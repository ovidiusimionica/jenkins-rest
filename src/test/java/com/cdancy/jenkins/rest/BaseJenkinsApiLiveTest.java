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

package com.cdancy.jenkins.rest;

import static com.cdancy.jenkins.rest.TestUtilities.inferTestAuthentication;
import static java.lang.Boolean.TRUE;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.cdancy.jenkins.rest.JenkinsApi.Builder;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.parsers.ResponseResult;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testng.annotations.Test;


@Test(groups = "live")
public class BaseJenkinsApiLiveTest
{

    public static final String ENDPOINT;

    static
    {
        GenericContainer redis = new GenericContainer(
            new ImageFromDockerfile().withDockerfile(
                Path.of(BaseJenkinsApiLiveTest.class.getResource("/docker/Dockerfile").getPath())));
        redis.withExposedPorts(8080).waitingFor(new HttpWaitStrategy().forPort(8080).forStatusCode(403)).start();
        ENDPOINT = "http://" + redis.getContainerInfo().getNetworkSettings().getIpAddress() + ":8080";
    }

    protected final JenkinsApi api;

    public BaseJenkinsApiLiveTest()
    {
        this.api = new Builder()
            .credentials(inferTestAuthentication())
            .properties(Collections.singletonMap("microprofile.rest.client.disable.default.mapper", true))
            .endpoint(ENDPOINT)
            .build();
    }

    protected String randomString()
    {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    protected String payloadFromResource(String resourcePath)
    {
        try (var is = Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)))
        {
            return new String(is.readAllBytes(), UTF_8);
        } catch (IOException e)
        {
            throw new RuntimeException("Unable to load resource: " + resourcePath, e);
        }
    }

    /**
     * Return a queue item that is being built.
     * If the queue item is canceled before the build is launched, null is returned.
     * To prevent the test from hanging, this method times out after 10 attempts and the queue item is returned the way it is.
     *
     * @param queueId The queue id returned when asking Jenkins to run a build.
     * @return Null if the queue item has been canceled before it has had a chance to run,
     * otherwise the QueueItem element is returned, but this does not guarantee that the build runs.
     * The caller has to check the value of queueItem.executable, and if it is null, the queue item is still pending.
     */
    protected QueueItem getRunningQueueItem(long queueId) throws InterruptedException
    {
        int attempts = 10;
        ResponseResult<QueueItem> result = api.queueApi().queueItem(queueId);
        QueueItem queueItem = result.getEntity();

        while (attempts-- > 0)
        {
            if (queueItem == null)
            {
                break;
            }
            if (TRUE.equals(queueItem.isCancelled()))
            {
                return null;
            }
            if (queueItem.getExecutable() != null)
            {
                return queueItem;
            }

            Thread.sleep(2000);
            queueItem = api.queueApi().queueItem(queueId).getEntity();
        }
        return queueItem;
    }

    protected BuildInfo getCompletedBuild(String jobName, QueueItem queueItem) throws InterruptedException
    {
        int attempts = 10;
        BuildInfo buildInfo = api.jobsApi()
            .buildInfo(null, jobName, queueItem.getExecutable().getNumber())
            .getEntity();

        while (buildInfo != null && buildInfo.getResult() == null && attempts-- > 0)
        {
            Thread.sleep(2000);
            buildInfo = api.jobsApi()
                .buildInfo(null, jobName, queueItem.getExecutable().getNumber())
                .getEntity();
        }
        return buildInfo;
    }

}
