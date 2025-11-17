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
import com.cdancy.jenkins.rest.domain.job.Action;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.Cause;
import com.cdancy.jenkins.rest.domain.job.Job;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.job.JobList;
import com.cdancy.jenkins.rest.domain.job.Parameter;
import com.cdancy.jenkins.rest.domain.job.ProgressiveText;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.parsers.ResponseResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "JobsApiLiveTest", singleThreaded = true)
public class JobsApiLiveTest extends BaseJenkinsApiLiveTest
{

    private Long queueId;
    private Long queueIdForAnotherJob;
    private Integer buildNumber;
    private static final String FOLDER_PLUGIN_VERSION = "latest";

    private static final String FREESTYLE_JOB_NAME = "FreeStyleSleep";
    private static final String PIPELINE_JOB_NAME = "PipelineSleep";
    private static final String PIPELINE_WITH_ARTIFACT_JOB_NAME = "PipelineArtifact";
    private static final String PIPELINE_WITH_PARAM_JOB_NAME = "PipelineSleepParam";


    @AfterClass
    public void cleanup()
    {
        api().deleteRootJob("DevTest");
        api().deleteRootJob(FREESTYLE_JOB_NAME);
        api().deleteRootJob(PIPELINE_JOB_NAME);
        api().deleteRootJob(PIPELINE_WITH_PARAM_JOB_NAME);
        api().deleteRootJob(PIPELINE_WITH_ARTIFACT_JOB_NAME);
    }

    @Test
    public void testCreateJob()
    {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        var success = api().create(null, "DevTest", config);
        assertTrue(success.isSuccess());
    }

    // The next 3 tests must run one after the other as they use the same Job
    @Test
    public void testStopFreeStyleBuild() throws InterruptedException
    {
        String config = payloadFromResource("/freestyle-project-sleep-10-task.xml");
        var createStatus = api().create(null, FREESTYLE_JOB_NAME, config);
        assertTrue(createStatus.isSuccess());
        testStopJob(FREESTYLE_JOB_NAME);
    }

    private void testKillJob(String jobName) throws InterruptedException
    {
        testJobAction(jobName, (buildNumber) -> api().kill("", jobName, buildNumber));
    }

    private void testTermJob(String jobName) throws InterruptedException
    {
        testJobAction(jobName, (buildNumber) -> api().term("", jobName, buildNumber));
    }

    private void testStopJob(String jobName) throws InterruptedException
    {
        testJobAction(jobName, (buildNumber) -> api().stop("", jobName, buildNumber));
    }

    private void testJobAction(String jobName, Function<Integer, ResponseResult<Void>> action)
        throws InterruptedException
    {
        var qIdResult = api().build(null, jobName);
        assertTrue(qIdResult.isSuccess());
        var qId = qIdResult.getEntity();
        assertNotNull(qId);
        assertTrue(qId > 0);
        QueueItem queueItem = getRunningQueueItem(qId);
        assertNotNull(queueItem);
        assertNotNull(queueItem.getExecutable());
        Integer buildNumber = queueItem.getExecutable().getNumber();
        assertNotNull(buildNumber);

        var actionStatus = action.apply(buildNumber);
        assertTrue(actionStatus.isSuccess());
        BuildInfo buildInfo = getCompletedBuild(jobName, queueItem);
        assertEquals(buildInfo.getResult(), "ABORTED");
    }

    @Test(dependsOnMethods = "testStopFreeStyleBuild")
    public void testTermFreeStyleBuild() throws InterruptedException
    {
        var qId = api().build(null, FREESTYLE_JOB_NAME).getEntity();
        assertNotNull(qId);
        assertTrue(qId > 0);
        QueueItem queueItem = getRunningQueueItem(qId);
        assertNotNull(queueItem);
        assertNotNull(queueItem.getExecutable());
        Integer buildNumber = queueItem.getExecutable().getNumber();
        assertNotNull(buildNumber);
        var termStatus = api().term(null, FREESTYLE_JOB_NAME, buildNumber);
        // Strangely, term does not work on FreeStyleBuild
        assertTrue(termStatus.isSuccess());
        api().stop(null, FREESTYLE_JOB_NAME, buildNumber);
        BuildInfo buildInfoStop = getCompletedBuild(FREESTYLE_JOB_NAME, queueItem);
        assertEquals(buildInfoStop.getResult(), "ABORTED");
    }

    @Test(dependsOnMethods = "testTermFreeStyleBuild")
    public void testKillFreeStyleBuild() throws InterruptedException
    {
        var qId = api().build(null, FREESTYLE_JOB_NAME).getEntity();
        assertNotNull(qId);
        assertTrue(qId > 0);
        QueueItem queueItem = getRunningQueueItem(qId);
        assertNotNull(queueItem);
        assertNotNull(queueItem.getExecutable());
        Integer buildNumber = queueItem.getExecutable().getNumber();
        assertNotNull(buildNumber);
        var killStatus = api().kill(null, FREESTYLE_JOB_NAME, buildNumber);
        // Strangely, kill does not work on FreeStyleBuild
        assertTrue(killStatus.isSuccess());
        api().stop(null, FREESTYLE_JOB_NAME, buildNumber);
        BuildInfo buildInfoStop = getCompletedBuild(FREESTYLE_JOB_NAME, queueItem);
        assertEquals(buildInfoStop.getResult(), "ABORTED");
    }

    //
    // The next 3 tests must run one after the other as they use the same Job
    @Test
    public void testStopPipelineBuild() throws InterruptedException
    {
        String config = payloadFromResource("/pipeline.xml");
        var createStatus = api().create(null, PIPELINE_JOB_NAME, config);
        assertTrue(createStatus.isSuccess());
        testStopJob(PIPELINE_JOB_NAME);
    }

    @Test(dependsOnMethods = "testStopPipelineBuild")
    public void testTermPipelineBuild() throws InterruptedException
    {
        testTermJob(PIPELINE_JOB_NAME);
    }

    @Test(dependsOnMethods = "testTermPipelineBuild")
    public void testKillPipelineBuild() throws InterruptedException
    {
        testKillJob(PIPELINE_JOB_NAME);
    }

    @Test(dependsOnMethods = {"testTermPipelineBuild"})
    public void testGetJobListFromRoot()
    {
        var reponse = api().jobList("");
        assertTrue(reponse.isSuccess());
        JobList output = reponse.getEntity();
        assertNotNull(output);
        assertFalse(output.getJobs().isEmpty());
        assertEquals(output.getJobs().size(), 7);
    }

    @Test(dependsOnMethods = "testCreateJob")
    public void testGetJobInfo()
    {
        JobInfo output = api().jobInfo(null, "DevTest").getEntity();
        assertNotNull(output);
        assertEquals(output.getName(), "DevTest");
        assertNull(output.getLastBuild());
        assertNull(output.getFirstBuild());
        assertTrue(output.getBuilds().isEmpty());
    }

    @Test(dependsOnMethods = "testGetJobInfo")
    public void testLastBuildNumberOnJobWithNoBuilds()
    {
        var output = api().lastBuildNumber(null, "DevTest");
        assertFalse(output.isSuccess());
        assertNull(output.getEntity());
        assertEquals(output.getStatus(), NOT_FOUND.getStatusCode());
    }

    @Test(dependsOnMethods = "testLastBuildNumberOnJobWithNoBuilds")
    public void testLastBuildTimestampOnJobWithNoBuilds()
    {
        var output = api().lastBuildTimestamp(null, "DevTest");
        assertFalse(output.isSuccess());
        assertNull(output.getEntity());
        assertEquals(output.getStatus(), NOT_FOUND.getStatusCode());
    }

    @Test(dependsOnMethods = "testLastBuildTimestampOnJobWithNoBuilds")
    public void testBuildJob() throws InterruptedException
    {
        queueId = api().build(null, "DevTest").getEntity();
        assertTrue(queueId > 0);
        // Before we exit the test, wait until the job runs
        QueueItem queueItem = getRunningQueueItem(queueId);
        getCompletedBuild("DevTest", queueItem);
    }

    @Test(dependsOnMethods = "testBuildJob")
    public void testLastBuildNumberOnJob()
    {
        buildNumber = api().lastBuildNumber(null, "DevTest").getEntity();
        assertNotNull(buildNumber);
        assertEquals(buildNumber, 1);
    }

    @Test(dependsOnMethods = "testLastBuildNumberOnJob")
    public void testLastBuildTimestamp()
    {
        String output = api().lastBuildTimestamp(null, "DevTest").getEntity();
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestamp")
    public void testLastBuildGetProgressiveText()
    {
        ProgressiveText output = api().progressiveText(null, "DevTest", 0).getEntity();
        assertNotNull(output);
        assertTrue(output.getSize() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test(dependsOnMethods = "testLastBuildGetProgressiveText")
    public void testGetBuildInfo()
    {
        BuildInfo output = api().buildInfo(null, "DevTest", buildNumber).getEntity();
        assertNotNull(output);
        assertEquals("DevTest #" + buildNumber, output.getFullDisplayName());
        assertEquals(queueId, output.getQueueId());
    }

    @Test(dependsOnMethods = "testGetBuildInfo")
    public void testGetBuildParametersOfLastJob()
    {
        List<Parameter> parameters =
            api().buildInfo(null, "DevTest", 1).getEntity().getActions().getFirst().getParameters();
        assertEquals(parameters.size(), 0);
    }

    @Test
    public void testBuildInfoActions() throws InterruptedException
    {
        String config = payloadFromResource("/pipeline-with-param.xml");
        var createStatus = api().create(null, PIPELINE_WITH_PARAM_JOB_NAME, config);
        assertTrue(createStatus.isSuccess());
        var qId =
            api().buildWithParameters(null, PIPELINE_WITH_PARAM_JOB_NAME,
                Map.of("MY_PARAM", List.of("param_value_1"))).getEntity();
        assertNotNull(qId);
        assertTrue(qId > 0);
        QueueItem queueItem = getRunningQueueItem(qId);
        assertNotNull(queueItem);
        assertNotNull(queueItem.getExecutable());
        assertNotNull(queueItem.getExecutable().getNumber());
        BuildInfo buildInfo = getCompletedBuild(PIPELINE_WITH_PARAM_JOB_NAME, queueItem);
        assertEquals(buildInfo.getResult(), "SUCCESS");

        Optional<Action> paramAction = buildInfo.getActions().stream().filter(action -> {
                return "hudson.model.ParametersAction".equals(action.getClazz());
            }
        ).findFirst();

        assertTrue(paramAction.isPresent());
        assertEquals(paramAction.get().getParameters(), List.of(
            new Parameter("hudson.model.StringParameterValue", "MY_PARAM", "param_value_1")
        ));
    }

    @Test(dependsOnMethods = "testGetBuildParametersOfLastJob")
    public void testCreateJobThatAlreadyExists()
    {
        String config = payloadFromResource("/freestyle-project.xml");
        var success = api().create(null, "DevTest", config);
        assertFalse(success.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateJobThatAlreadyExists")
    public void testSetDescription()
    {
        var successResponse = api().description(null, "DevTest", "RandomDescription");
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testSetDescription")
    public void testGetDescription()
    {
        String output = api().description(null, "DevTest").getEntity();
        assertEquals(output, "RandomDescription");
    }

    @Test(dependsOnMethods = "testGetDescription")
    public void testGetConfig()
    {
        String output = api().config(null, "DevTest").getEntity();
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testGetConfig")
    public void testUpdateConfig()
    {
        String config = payloadFromResource("/freestyle-project.xml");
        var successResponse = api().config(null, "DevTest", config);
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testUpdateConfig")
    public void testBuildJobWithParameters()
    {
        var output = api().buildWithParameters(null, "DevTest",
            Map.of(
                "SomeKey", List.of("SomeVeryNewValue")
            ));
        assertTrue(output.isSuccess());
        assertTrue(output.getEntity() > 0);
    }

    @Test(dependsOnMethods = "testBuildJobWithParameters")
    public void testBuildJobWithNullParametersMap()
    {
        var output = api().buildWithParameters(null, "DevTest", null);
        assertTrue(output.isSuccess());
        assertTrue(output.getEntity() > 0);
    }

    @Test(dependsOnMethods = "testBuildJobWithNullParametersMap")
    public void testBuildJobWithEmptyParametersMap()
    {
        var output = api().buildWithParameters(null, "DevTest", new HashMap<>());
        assertFalse(output.isSuccess());
    }

    @Test(dependsOnMethods = "testBuildJobWithEmptyParametersMap")
    public void testDisableJob()
    {
        var successResponse = api().disable(null, "DevTest");
        assertTrue(successResponse.isSuccess());

    }

    @Test(dependsOnMethods = "testDisableJob")
    public void testDisableJobAlreadyDisabled()
    {
        var successResponse = api().disable(null, "DevTest");
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testDisableJobAlreadyDisabled")
    public void testEnableJob()
    {
        var successResponse = api().enable(null, "DevTest");
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testEnableJob")
    public void testEnableJobAlreadyEnabled()
    {
        var successResponse = api().enable(null, "DevTest");
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testEnableJobAlreadyEnabled")
    public void testRenameJob()
    {
        var successResponse = api().rename(null, "DevTest", "NewDevTest");
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testRenameJob")
    public void testRenameJobNotExist()
    {
        var successResponse = api().rename(null, "JobNotExist", "NewDevTest");
        assertFalse(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testRenameJobNotExist")
    public void testDeleteJob()
    {
        var successResponse = api().delete(null, "NewDevTest");
        assertTrue(successResponse.isSuccess());
    }

    @Test(dependsOnMethods = "testDeleteJob")
    public void testDeleteJobNotExists()
    {
        var successResponse = api().delete(null, "JobNotExist");
        assertFalse(successResponse.isSuccess());
    }


    public void testCreateFoldersInJenkins()
    {
        String config = payloadFromResource("/folder-config.xml");
        var success1 = api().create(null, "test-folder", config);
        assertTrue(success1.isSuccess());
        var success2 = api().create("test-folder", "test-folder-1", config);
        assertTrue(success2.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateFoldersInJenkins")
    public void testCreateJobInFolder()
    {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        var success = api().create("test-folder/test-folder-1", "JobInFolder", config);
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateFoldersInJenkins")
    public void testCreateJobWithIncorrectFolderPath()
    {
        String config = payloadFromResource("/folder-config.xml");
        var success = api().create("/test-folder/not-existing-segment/test-folder-1/", "Job", config);
        assertFalse(success.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateJobInFolder")
    public void testGetJobListInFolder()
    {
        JobList output = api().jobList("test-folder/test-folder-1").getEntity();
        assertNotNull(output);
        assertFalse(output.getJobs().isEmpty());
        assertEquals(output.getJobs().size(), 1);
        assertEquals(output.getJobs().getFirst(), new Job("hudson.model.FreeStyleProject", "JobInFolder",
            ENDPOINT + "/job/test-folder/job/test-folder-1/job/JobInFolder/",
            "notbuilt"));
    }

    @Test(dependsOnMethods = "testCreateJobInFolder")
    public void testUpdateJobConfigInFolder()
    {
        String config = payloadFromResource("/freestyle-project.xml");
        var success = api().config("test-folder/test-folder-1", "JobInFolder", config);
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testUpdateJobConfigInFolder")
    public void testDisableJobInFolder()
    {
        var success = api().disable("test-folder/test-folder-1", "JobInFolder");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testDisableJobInFolder")
    public void testEnableJobInFolder()
    {
        var success = api().enable("test-folder/test-folder-1", "JobInFolder");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testEnableJobInFolder")
    public void testSetDescriptionOfJobInFolder()
    {
        var success = api().description("test-folder/test-folder-1", "JobInFolder", "RandomDescription");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testSetDescriptionOfJobInFolder")
    public void testGetDescriptionOfJobInFolder()
    {
        var output = api().description("test-folder/test-folder-1", "JobInFolder");
        assertEquals(output.getEntity(), "RandomDescription");
    }

    @Test(dependsOnMethods = "testGetDescriptionOfJobInFolder")
    public void testGetJobInfoInFolder()
    {
        JobInfo output = api().jobInfo("test-folder/test-folder-1", "JobInFolder").getEntity();
        assertNotNull(output);
        assertEquals(output.getName(), "JobInFolder");
        assertTrue(output.getBuilds().isEmpty());
    }

    @Test(dependsOnMethods = "testGetJobInfoInFolder")
    public void testBuildWithParameters() throws InterruptedException
    {
        queueIdForAnotherJob = api().buildWithParameters("test-folder/test-folder-1", "JobInFolder",
            Map.of(
                "SomeKey", List.of("SomeVeryNewValue")
            )).getEntity();
        assertNotNull(queueIdForAnotherJob);
        assertTrue(queueIdForAnotherJob > 0);
        QueueItem queueItem = getRunningQueueItem(queueIdForAnotherJob);
        assertNotNull(queueItem);
    }

    @Test(dependsOnMethods = "testBuildWithParameters")
    public void testLastBuildTimestampOfJobInFolder()
    {
        String output = api().lastBuildTimestamp("test-folder/test-folder-1", "JobInFolder").getEntity();
        assertNotNull(output);
    }

    @Test(dependsOnMethods = "testLastBuildTimestampOfJobInFolder")
    public void testGetProgressiveText()
    {
        ProgressiveText output = api().progressiveText("test-folder/test-folder-1", "JobInFolder", 0).getEntity();
        assertNotNull(output);
        assertTrue(output.getSize() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildInfoOfJobInFolder()
    {
        BuildInfo output = api().buildInfo("test-folder/test-folder-1", "JobInFolder", 1).getEntity();
        assertNotNull(output);
        assertTrue(output.getFullDisplayName().contains("JobInFolder #1"));
        assertEquals(queueIdForAnotherJob, output.getQueueId());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildParametersofJob()
    {
        List<Parameter> parameters =
            api().buildInfo("test-folder/test-folder-1", "JobInFolder", 1).getEntity().getActions().getFirst()
                .getParameters();
        assertNotNull(parameters);
        assertEquals(parameters.getFirst().name(), "SomeKey");
        assertEquals(parameters.getFirst().value(), "SomeVeryNewValue");
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetBuildCausesOfJob()
    {
        List<Cause> causes =
            api().buildInfo("test-folder/test-folder-1", "JobInFolder", 1)
                .getEntity().getActions().get(1).getCauses();
        assertNotNull(causes);
        assertTrue(!causes.isEmpty());
        assertNotNull(causes.getFirst().getShortDescription());
        assertNotNull(causes.getFirst().getUserId());
        assertNotNull(causes.getFirst().getUserName());
    }

    @Test(dependsOnMethods = "testGetProgressiveText")
    public void testGetProgressiveTextOfBuildNumber()
    {
        ProgressiveText output = api().progressiveText("test-folder/test-folder-1", "JobInFolder", 1, 0).getEntity();
        assertNotNull(output);
        assertTrue(output.getSize() > 0);
        assertFalse(output.hasMoreData());
    }

    @Test
    public void testCreateJobForEmptyAndNullParams()
    {
        String config = payloadFromResource("/freestyle-project-empty-and-null-params.xml");
        var success = api().create(null, "JobForEmptyAndNullParams", config);
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateJobForEmptyAndNullParams")
    public void testBuildWithParametersOfJobForEmptyAndNullParams() throws InterruptedException
    {
        Map<String, List<String>> hashMap = HashMap.newHashMap(2);
        hashMap.put("SomeKey1", List.of(""));
        hashMap.put("SomeKey2", null);
        var job1 = api.jobsApi().buildWithParameters(null, "JobForEmptyAndNullParams", hashMap);
        assertNotNull(job1.getEntity());
        assertTrue(job1.getEntity() > 0);
        QueueItem queueItem = getRunningQueueItem(job1.getEntity());
        assertNotNull(queueItem);
    }

    @Test(dependsOnMethods = "testBuildWithParametersOfJobForEmptyAndNullParams")
    public void testGetBuildParametersOfJobForEmptyAndNullParams()
    {
        List<Parameter> parameters =
            api().buildInfo(null, "JobForEmptyAndNullParams", 1)
                .getEntity().getActions().getFirst().getParameters();
        assertNotNull(parameters);
        assertEquals(parameters.get(0).name(), "SomeKey1");
        assertTrue(parameters.get(0).value().isEmpty());
        assertEquals(parameters.get(1).name(), "SomeKey2");
        assertTrue(parameters.get(1).value().isEmpty());
    }

    @Test(dependsOnMethods = {"testGetBuildParametersOfJobForEmptyAndNullParams", "testGetJobListFromRoot"})
    public void testDeleteJobForEmptyAndNullParams()
    {
        var success = api().delete(null, "JobForEmptyAndNullParams");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateFoldersInJenkins")
    public void testCreateJobWithLeadingAndTrailingForwardSlashes()
    {
        String config = payloadFromResource("/freestyle-project-no-params.xml");
        var success = api().create("/test-folder/test-folder-1/", "Job", config);
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testCreateJobWithLeadingAndTrailingForwardSlashes")
    public void testDeleteJobWithLeadingAndTrailingForwardSlashes()
    {
        var success = api().delete("/test-folder/test-folder-1/", "Job");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testGetBuildInfoOfJobInFolder")
    public void testRenameJonInFloder()
    {
        var success = api().rename("test-folder/test-folder-1", "JobInFolder", "NewJobInFolder");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testRenameJonInFloder")
    public void testDeleteJobInFolder()
    {
        var success = api().delete("test-folder/test-folder-1", "NewJobInFolder");
        assertTrue(success.isSuccess());
    }

    @Test(dependsOnMethods = "testDeleteJobInFolder")
    public void testDeleteFolders()
    {
        var success1 = api().delete("test-folder", "test-folder-1");
        assertTrue(success1.isSuccess());
        var success2 = api().delete(null, "test-folder");
        assertTrue(success2.isSuccess());
    }

    @Test
    public void testGetJobInfoNonExistentJob()
    {
        JobInfo output = api().jobInfo(null, randomString()).getEntity();
        assertNull(output);
    }

    @Test
    public void testDeleteJobNonExistent()
    {
        var success = api().delete(null, randomString());
        assertFalse(success.isSuccess());
    }

    @Test
    public void testGetConfigNonExistentJob()
    {
        String output = api().config(null, randomString()).getEntity();
        assertNull(output);
    }

    @Test
    public void testSetDescriptionNonExistentJob()
    {
        var success = api().description(null, randomString(), "RandomDescription");
        assertFalse(success.isSuccess());
    }

    @Test
    public void testGetDescriptionNonExistentJob()
    {
        String output = api().description(null, randomString()).getEntity();
        assertNull(output);
    }

    @Test
    public void testBuildNonExistentJob()
    {
        var output = api().build(null, randomString());
        assertNotNull(output);
        assertNull(output.getEntity());
        assertFalse(output.getError().isEmpty());
        assertEquals(output.getStatus(), NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetBuildInfoNonExistentJob()
    {
        BuildInfo output = api().buildInfo(null, randomString(), 123).getEntity();
        assertNull(output);
    }

    @Test
    public void testBuildNonExistentJobWithParams()
    {
        var output = api().buildWithParameters(null, randomString(),
            Map.of(
                "SomeKey", List.of("SomeVeryNewValue")
            ));

        assertFalse(output.getError().isEmpty());
        assertEquals(output.getStatus(), NOT_FOUND.getStatusCode());
    }


    @Test
    public void testPipelineArtifact() throws InterruptedException, IOException
    {
        String config = payloadFromResource("/pipeline-with-artifact.xml");
        var createStatus = api().create(null, PIPELINE_WITH_ARTIFACT_JOB_NAME, config);
        assertTrue(createStatus.isSuccess());

        long artifactBuildId = api().build(null, PIPELINE_WITH_ARTIFACT_JOB_NAME).getEntity();
        assertTrue(artifactBuildId > 0);
        // Before we exit the test, wait until the job runs
        QueueItem queueItem = getRunningQueueItem(artifactBuildId);
        var buildInfo = getCompletedBuild(PIPELINE_WITH_ARTIFACT_JOB_NAME, queueItem);
        var artifact = api().artifactInFolder(null, PIPELINE_WITH_ARTIFACT_JOB_NAME, buildInfo.getNumber(), buildInfo.getArtifacts().getFirst().getRelativePath());
        assertTrue(artifact.isSuccess());
        assertEquals(new String(artifact.getEntity().readAllBytes()), "Hello artifact!\n");
    }


    private JobsApi api()
    {
        return api.jobsApi();
    }
}
