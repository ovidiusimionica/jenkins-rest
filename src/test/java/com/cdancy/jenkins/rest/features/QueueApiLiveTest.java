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

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Test(groups = "live", testName = "QueueApiLiveTest", singleThreaded = true)
public class QueueApiLiveTest extends BaseJenkinsApiLiveTest
{

    @BeforeClass
    public void init()
    {
        String config = payloadFromResource("/freestyle-project-sleep-task.xml");
        var success = api.jobsApi().create(null, "QueueTest", config);
        assertTrue(success.isSuccess());

        config = payloadFromResource("/freestyle-project.xml");
        success = api.jobsApi().create(null, "QueueTestSingleParam", config);
        assertTrue(success.isSuccess());

        config = payloadFromResource("/freestyle-project-sleep-task-multiple-params.xml");
        success = api.jobsApi().create(null, "QueueTestMultipleParams", config);
        assertTrue(success.isSuccess());
    }

    @Test
    public void testGetQueue()
    {
        var job1Response = api.jobsApi().build(null, "QueueTest");
        assertTrue(job1Response.isSuccess());
        var job2Response = api.jobsApi().build(null, "QueueTest");
        assertTrue(job2Response.isSuccess());
        List<QueueItem> queueItems = api().queue().getEntity();
        assertFalse(queueItems.isEmpty());
        boolean foundLastKickedJob = false;
        for (QueueItem item : queueItems)
        {
            if (item.getId() == job2Response.getEntity())
            {
                foundLastKickedJob = true;
                break;
            }
        }
        assertTrue(foundLastKickedJob);
    }

    @Test
    public void testGetPendingQueueItem()
    {
        var job1 = api.jobsApi().build(null, "QueueTest");
        var job2 = api.jobsApi().build(null, "QueueTest");

        // job2 is queue after job1, so while job1 runs, job2 is pending in the queue
        var queueItemResponse = api().queueItem(job2.getEntity());
        assertTrue(queueItemResponse.isSuccess());
        var queueItem = queueItemResponse.getEntity();
        assertFalse(queueItem.isCancelled());
        assertNotNull(queueItem.getWhy());
        assertNull(queueItem.getExecutable());
    }

    @Test
    public void testGetRunningQueueItem() throws InterruptedException
    {
        var job1 = api.jobsApi().build(null, "QueueTest");
        var job2 = api.jobsApi().build(null, "QueueTest");

        // job1 runs first, so we get its queueItem
        QueueItem queueItem = getRunningQueueItem(job1.getEntity());

        // If null, it means the queueItem has been cancelled, which would not be normal in this test
        assertNotNull(queueItem);
        assertFalse(queueItem.isCancelled());

        //  We exepect this build to run, consequently:
        //  * the why field should now be null
        //  * the executable field should NOT be null
        //  * the build number should be set to an integer
        //  * the url for the build should be set to a string
        assertNull(queueItem.getWhy());
        assertNotNull(queueItem.getExecutable());
    }

    @Test
    public void testQueueItemSingleParameters() throws InterruptedException
    {
        var job1Response = api.jobsApi().buildWithParameters(null, "QueueTestSingleParam",
            Map.of(
                "SomeKey", List.of("SomeVeryNewValue1")
            ));
        assertTrue(job1Response.isSuccess());
        assertTrue(job1Response.getEntity() > 0);

        // Jenkins will reject two consecutive build requests when the build parameter values are the same
        // So we must set some different parameter values
        var job2Response = api.jobsApi().buildWithParameters(null, "QueueTestSingleParam",
            Map.of(
                "SomeKey", List.of("SomeVeryNewValue2")
            ));
        assertTrue(job2Response.isSuccess());
        assertTrue(job2Response.getEntity() > 0);


        QueueItem queueItem = getRunningQueueItem(job1Response.getEntity());
        assertNotNull(queueItem);
        assertFalse(queueItem.isCancelled());

        assertEquals(queueItem.getParams(), Map.of("SomeKey", "SomeVeryNewValue1"));
    }

    @Test
    public void testQueueItemMultipleParameters() throws InterruptedException
    {
        var job1Response = api.jobsApi().buildWithParameters(null, "QueueTestMultipleParams",
            Map.of(
                "SomeKey1", List.of("SomeVeryNewValue1")
            )
        );
        assertTrue(job1Response.isSuccess());
        assertTrue(job1Response.getEntity() > 0);

        // Jenkins will reject two consecutive build requests when the build parameter values are the same
        // So we must set some different parameter values
        var job2Response = api.jobsApi().buildWithParameters(null, "QueueTestMultipleParams",
            Map.of(
                "SomeKey1", List.of("SomeVeryNewValue2")
            )
        );
        assertTrue(job2Response.isSuccess());
        assertTrue(job2Response.getEntity() > 0);


        QueueItem queueItem = getRunningQueueItem(job1Response.getEntity());
        assertNotNull(queueItem);
        assertFalse(queueItem.isCancelled());

        assertEquals(queueItem.getParams(), Map.of(
            "SomeKey1", "SomeVeryNewValue1",
            "SomeKey2", "SomeValue2",
            "SomeKey3", "SomeValue3"
        ));
    }

    @Test
    public void testQueueItemEmptyParameterValue() throws InterruptedException
    {
        var job1Response = api.jobsApi().buildWithParameters(null, "QueueTestMultipleParams",
            Map.of(
                "SomeKey1", List.of("")
            ));
        assertTrue(job1Response.isSuccess());
        assertTrue(job1Response.getEntity() > 0);


        QueueItem queueItem = getRunningQueueItem(job1Response.getEntity());
        assertNotNull(queueItem);

        assertEquals(queueItem.getParams(), Map.of(
            "SomeKey1", "",
            "SomeKey2", "SomeValue2",
            "SomeKey3", "SomeValue3"
        ));
    }

    @Test
    public void testGetCancelledQueueItem()
    {
        var job1 = api.jobsApi().build(null, "QueueTest");
        var job2 = api.jobsApi().build(null, "QueueTest");

        var success = api().cancel(job2.getEntity());
        assertTrue(success.isSuccess());


        var queueItemResponse = api().queueItem(job2.getEntity());
        assertTrue(queueItemResponse.isSuccess());
        var queueItem = queueItemResponse.getEntity();
        assertTrue(queueItem.isCancelled());
        assertNull(queueItem.getWhy());
        assertNull(queueItem.getExecutable());
    }

    @Test
    public void testCancelNonExistentQueueItem()
    {
        var success = api().cancel(123456789);
        assertFalse(success.isSuccess());
        assertEquals(success.getStatus(), NOT_FOUND.getStatusCode());
        assertNotNull(success.getError());
    }

    @AfterClass
    public void finish()
    {
        var success = api.jobsApi().delete(null, "QueueTest");
        assertTrue(success.isSuccess());

        success = api.jobsApi().delete(null, "QueueTestSingleParam");
        assertTrue(success.isSuccess());

        success = api.jobsApi().delete(null, "QueueTestMultipleParams");
        assertTrue(success.isSuccess());
    }

    private QueueApi api()
    {
        return api.queueApi();
    }
}
