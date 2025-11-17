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
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofInt;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofProgressiveText;
import static com.cdancy.jenkins.rest.parsers.ResponseResult.ofVoid;

import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.job.JobInfo;
import com.cdancy.jenkins.rest.domain.job.JobList;
import com.cdancy.jenkins.rest.domain.job.PipelineNode;
import com.cdancy.jenkins.rest.domain.job.PipelineNodeLog;
import com.cdancy.jenkins.rest.domain.job.ProgressiveText;
import com.cdancy.jenkins.rest.domain.job.Workflow;
import com.cdancy.jenkins.rest.parsers.ResponseResult;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/")
public interface JobsApi
{

    static final Pattern BUILD_QUEUE_PATTERN = Pattern.compile("^.*/queue/item/(\\d+)/$");

    @Path("api/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    Response jobListRaw();


    @Path("/{folderPath}/api/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    Response jobListInFolderRaw(@PathParam("folderPath") String folderPath);

    default ResponseResult<JobList> jobList(String folderPath)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? jobListRaw()
            : jobListInFolderRaw(appendFolderPrefixIfNeeded((folderPath)));
        return of(response, JobList.class);
    }

    // for jobs directly under root
    @GET
    @Path("job/{name}/api/json")
    Response jobInfoRaw(@PathParam("name") String jobName);

    // for jobs in folders
    @GET
    @Path("/{folderPath}/job/{name}/api/json")
    Response jobInfoInFolderRaw(@PathParam("folderPath") String folderPath,
                                @PathParam("name") String jobName);


    // for jobs directly under root
    default ResponseResult<JobInfo> jobInfo(String folderPath, String jobName)
    {
        ResponseResult<JobInfo> response = null;
        if (folderPath == null || folderPath.isEmpty())
        {
            response = of(jobInfoRaw(jobName), JobInfo.class);
        } else
        {
            response = of(jobInfoInFolderRaw(appendFolderPrefixIfNeeded(folderPath), jobName), JobInfo.class);
        }

        return response;
    }

    @Path("job/{name}/{number}/api/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    Response buildInfoRaw(@PathParam("name") String name,
                          @PathParam("number") int number);


    @Path("/{folderPath}/job/{name}/{number}/api/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    Response buildInfoInFolderRaw(@PathParam("folderPath") String folderPath,
                                  @PathParam("name") String name,
                                  @PathParam("number") int number);

    default ResponseResult<BuildInfo> buildInfo(String folderPath, String jobName, int buildNumber)
    {
        ResponseResult<BuildInfo> response = null;
        if (folderPath == null || folderPath.isEmpty())
        {
            response = of(buildInfoRaw(jobName, buildNumber), BuildInfo.class);
        } else
        {
            response = of(buildInfoInFolderRaw(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber), BuildInfo.class);
        }

        return response;
    }

    @GET
    @Path("job/{name}/{number}/artifact/{relativeArtifactPath}")
    @Consumes(MediaType.WILDCARD)
    Response artifactRaw(
        @PathParam("name") String jobName,
        @PathParam("number") int buildNumber,
        @PathParam("relativeArtifactPath") String relativeArtifactPath
    );

    // --- For jobs inside folders ---
    @GET
    @Path("/{folderPath}/job/{name}/{number}/artifact/{relativeArtifactPath}")
    @Consumes(MediaType.WILDCARD)
    Response artifactInFolderRaw(
        @PathParam("folderPath") String folderPath,
        @PathParam("name") String jobName,
        @PathParam("number") int buildNumber,
        @PathParam("relativeArtifactPath") String relativeArtifactPath
    );

    default ResponseResult<InputStream> artifactInFolder(
        String folderPath,
        String jobName,
        int buildNumber,
        String relativeArtifactPath
    )
    {
        ResponseResult<InputStream> response = null;
        if (folderPath == null || folderPath.isEmpty())
        {
            response = of(artifactRaw(jobName, buildNumber, relativeArtifactPath), InputStream.class);
        } else
        {
            response =
                of(artifactInFolderRaw(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber, relativeArtifactPath), InputStream.class);
        }

        return response;
    }

    @POST
    @Path("createItem")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    Response createRootJob(
        @QueryParam("name") String jobName,
        String configXML
    );

    @POST
    @Path("/{folderPath}/createItem")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    Response createJobInFolder(
        @PathParam("folderPath") String folderPath,
        @QueryParam("name") String jobName,
        String configXML
    );

    default ResponseResult<Void> create(String folderPath, String jobName, String configXML)
    {
        ResponseResult<Void> response = null;
        if (folderPath == null || folderPath.isEmpty())
        {
            response = ofVoid(createRootJob(jobName, configXML));
        } else
        {
            response = ofVoid(createJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, configXML));
        }

        return response;
    }

    private static String appendFolderPrefixIfNeeded(String folderPath)
    {
        String[] split = folderPath.split("/");
        StringBuilder path = new StringBuilder();
        boolean skipAppendNext = false;
        for (int index = 0; index < split.length; index++)
        {
            var currentSegment = split[index];
            if (currentSegment.isEmpty()) {
                continue;
            }
            if ("job".equals(currentSegment))
            {
                skipAppendNext = true;
            } else if (!skipAppendNext)
            {
                currentSegment = "job/" + currentSegment;
            } else
            {
                skipAppendNext = false;
            }

            if (index != split.length - 1)
            {
                currentSegment += "/";
            }

            path.append(currentSegment);
        }

        if (path.isEmpty())
        {
            return "job/" + folderPath;
        } else
        {
            return path.toString();
        }
    }

    @GET
    @Path("job/{name}/config.xml")
    @Consumes(MediaType.TEXT_PLAIN)
    Response getRootJobConfig(@PathParam("name") String jobName);

    @GET
    @Path("/{folderPath}/job/{name}/config.xml")
    @Consumes(MediaType.TEXT_PLAIN)
    Response getJobConfigInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String jobName);

    default ResponseResult<String> config(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? getRootJobConfig(jobName)
            : getJobConfigInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return of(response, String.class);
    }

    @POST
    @Path("job/{name}/config.xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    Response updateRootJobConfig(@PathParam("name") String jobName, String configXML);

    @POST
    @Path("/{folderPath}/job/{name}/config.xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    Response updateJobConfigInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String jobName,
                                     String configXML);

    default ResponseResult<Void> config(String folderPath, String jobName, String configXML)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? updateRootJobConfig(jobName, configXML)
            : updateJobConfigInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, configXML);
        return ofVoid(response);
    }

    @GET
    @Path("job/{name}/description")
    @Consumes(MediaType.TEXT_PLAIN)
    Response getRootJobDescription(@PathParam("name") String jobName);

    @GET
    @Path("/{folderPath}/job/{name}/description")
    @Consumes(MediaType.TEXT_PLAIN)
    Response getJobDescriptionInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String jobName);

    default ResponseResult<String> description(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? getRootJobDescription(jobName)
            : getJobDescriptionInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return of(response, String.class);
    }

    @POST
    @Path("job/{name}/description")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response setRootJobDescription(@PathParam("name") String jobName, @FormParam("description") String description);

    @POST
    @Path("/{folderPath}/job/{name}/description")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response setJobDescriptionInFolder(@PathParam("folderPath") String folderPath,
                                       @PathParam("name") String jobName,
                                       @FormParam("description") String description);

    default ResponseResult<Void> description(String folderPath, String jobName, String description)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? setRootJobDescription(jobName, description)
            : setJobDescriptionInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, description);
        return ofVoid(response);
    }


    @POST
    @Path("job/{name}/doDelete")
    @Consumes(MediaType.TEXT_HTML)
    Response deleteRootJob(@PathParam("name") String jobName);

    @POST
    @Path("/{folderPath}/job/{name}/doDelete")
    @Consumes(MediaType.TEXT_HTML)
    Response deleteJobInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String jobName);

    default ResponseResult<Void> delete(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? deleteRootJob(jobName)
            : deleteJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return ofVoid(response);
    }

    @POST
    @Path("job/{name}/enable")
    @Consumes(MediaType.TEXT_HTML)
    Response enableRootJob(@PathParam("name") String jobName);

    @POST
    @Path("/{folderPath}/job/{name}/enable")
    @Consumes(MediaType.TEXT_HTML)
    Response enableJobInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String jobName);

    default ResponseResult<Void> enable(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? enableRootJob(jobName)
            : enableJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return ofVoid(response);
    }

    @POST
    @Path("job/{name}/disable")
    @Consumes(MediaType.TEXT_HTML)
    Response disableRootJob(@PathParam("name") String name);

    @POST
    @Path("/{folderPath}/job/{name}/disable")
    @Consumes(MediaType.TEXT_HTML)
    Response disableJobInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String name);

    default ResponseResult<Void> disable(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? disableRootJob(jobName)
            : disableJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return ofVoid(response);
    }

    @POST
    @Path("job/{name}/build")
    @Consumes(MediaType.WILDCARD)
    Response buildRootJob(@PathParam("name") String jobName);

    @POST
    @Path("/{folderPath}/job/{name}/build")
    @Consumes(MediaType.WILDCARD)
    Response buildJobInFolder(@PathParam("folderPath") String folderPath, @PathParam("name") String jobName);

    default ResponseResult<Long> build(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? buildRootJob(jobName)
            : buildJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);

        return extractBuildNumberResponse(response);
    }

    private static ResponseResult<Long> extractBuildNumberResponse(Response response)
    {
        Long buildNumber = null;
        String error = null;
        String url = response.getHeaderString("Location");
        if (url != null)
        {
            Matcher matcher = BUILD_QUEUE_PATTERN.matcher(url);
            if (matcher.find() && matcher.groupCount() == 1)
            {
                buildNumber = Long.valueOf(matcher.group(1));
            }
        } else
        {
            error = "No queue item Location header could be found despite getting a valid HTTP response.";
        }

        return ResponseResult.of(response, buildNumber, error);
    }


    @POST
    @Path("job/{name}/buildWithParameters")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response buildRootJobWithParameters(@PathParam("name") String jobName, Form params);

    @POST
    @Path("/{folderPath}/job/{name}/buildWithParameters")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response buildJobWithParametersInFolder(@PathParam("folderPath") String folderPath,
                                            @PathParam("name") String jobName,
                                            Form params);

    default ResponseResult<Long> buildWithParameters(String folderPath,
                                                     String jobName,
                                                     Map<String, List<String>> params)
    {
        Form form = new Form();
        if (params != null)
        {
            params.forEach((k, v) -> {
                if (v != null && !v.isEmpty())
                {
                    v.forEach(val -> form.param(k, val));
                } else
                {
                    form.param(k, "");
                }
            });
        }

        Response response = (folderPath == null || folderPath.isEmpty())
            ? buildRootJobWithParameters(jobName, form)
            : buildJobWithParametersInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, form);
        return extractBuildNumberResponse(response);
    }

    @POST
    @Path("job/{name}/{number}/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    Response stopRootJob(@PathParam("name") String jobName, @PathParam("number") int buildNumber);

    @POST
    @Path("/{folderPath}/job/{name}/{number}/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    Response stopJobInFolder(@PathParam("folderPath") String folderPath,
                             @PathParam("name") String jobName,
                             @PathParam("number") int buildNumber);

    default ResponseResult<Void> stop(String folderPath, String jobName, int buildNumber)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? stopRootJob(jobName, buildNumber)
            : stopJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber);
        return ofVoid(response);
    }

    @POST
    @Path("job/{name}/{number}/term")
    @Consumes(MediaType.APPLICATION_JSON)
    Response termRootJob(@PathParam("name") String jobName, @PathParam("number") int buildNumber);

    @POST
    @Path("/{folderPath}/job/{name}/{number}/term")
    @Consumes(MediaType.APPLICATION_JSON)
    Response termJobInFolder(@PathParam("folderPath") String folderPath,
                             @PathParam("name") String jobName,
                             @PathParam("number") int buildNumber);

    default ResponseResult<Void> term(String folderPath, String jobName, int buildNumber)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? termRootJob(jobName, buildNumber)
            : termJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber);
        return ofVoid(response);
    }

    @POST
    @Path("job/{name}/{number}/kill")
    @Consumes(MediaType.APPLICATION_JSON)
    Response killRootJob(@PathParam("name") String jobName, @PathParam("number") int buildNumber);

    @POST
    @Path("/{folderPath}/job/{name}/{number}/kill")
    @Consumes(MediaType.APPLICATION_JSON)
    Response killJobInFolder(@PathParam("folderPath") String folderPath,
                             @PathParam("name") String jobName,
                             @PathParam("number") int buildNumber);

    default ResponseResult<Void> kill(String folderPath, String jobName, int buildNumber)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? killRootJob(jobName, buildNumber)
            : killJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber);
        return ofVoid(response);
    }

    @GET
    @Path("job/{name}/lastBuild/buildNumber")
    @Consumes(MediaType.TEXT_PLAIN)
    Response lastRootBuildNumber(@PathParam("name") String jobName);

    @GET
    @Path("/{folderPath}/job/{name}/lastBuild/buildNumber")
    @Consumes(MediaType.TEXT_PLAIN)
    Response lastBuildNumberInFolder(@PathParam("folderPath") String folderPath,
                                     @PathParam("name") String jobName);

    default ResponseResult<Integer> lastBuildNumber(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? lastRootBuildNumber(jobName)
            : lastBuildNumberInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);

        return ofInt(response);
    }

    @GET
    @Path("job/{name}/lastBuild/buildTimestamp")
    @Consumes(MediaType.TEXT_PLAIN)
    Response lastRootBuildTimestamp(@PathParam("name") String jobName);

    @GET
    @Path("/{folderPath}/job/{name}/lastBuild/buildTimestamp")
    @Consumes(MediaType.TEXT_PLAIN)
    Response lastBuildTimestampInFolder(@PathParam("folderPath") String folderPath,
                                        @PathParam("name") String jobName);

    default ResponseResult<String> lastBuildTimestamp(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? lastRootBuildTimestamp(jobName)
            : lastBuildTimestampInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return of(response, String.class);
    }

    @GET
    @Path("job/{name}/lastBuild/logText/progressiveText")
    @Consumes(MediaType.TEXT_PLAIN)
    Response progressiveRootText(@PathParam("name") String jobName, @QueryParam("start") int start);

    @GET
    @Path("/{folderPath}/job/{name}/lastBuild/logText/progressiveText")
    @Consumes(MediaType.TEXT_PLAIN)
    Response progressiveTextInFolder(@PathParam("folderPath") String folderPath,
                                     @PathParam("name") String jobName,
                                     @QueryParam("start") int start);

    default ResponseResult<ProgressiveText> progressiveText(String folderPath, String jobName, int start)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? progressiveRootText(jobName, start)
            : progressiveTextInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, start);
        return ofProgressiveText(response);
    }

    @GET
    @Path("job/{name}/{number}/logText/progressiveText")
    @Consumes(MediaType.TEXT_PLAIN)
    Response progressiveRootBuildText(@PathParam("name") String jobName,
                                      @PathParam("number") int buildNumber,
                                      @QueryParam("start") int start);

    @GET
    @Path("/{folderPath}/job/{name}/{number}/logText/progressiveText")
    @Consumes(MediaType.TEXT_PLAIN)
    Response progressiveBuildTextInFolder(@PathParam("folderPath") String folderPath,
                                          @PathParam("name") String jobName,
                                          @PathParam("number") int buildNumber,
                                          @QueryParam("start") int start);

    default ResponseResult<ProgressiveText> progressiveText(String folderPath,
                                                            String jobName,
                                                            int buildNumber,
                                                            int start)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? progressiveRootBuildText(jobName, buildNumber, start)
            : progressiveBuildTextInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber, start);
        return ofProgressiveText(response);
    }

    @POST
    @Path("job/{name}/doRename")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response renameRootJob(@PathParam("name") String jobName, @QueryParam("newName") String newName);

    @POST
    @Path("/{folderPath}/job/{name}/doRename")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response renameJobInFolder(@PathParam("folderPath") String folderPath,
                               @PathParam("name") String jobName,
                               @QueryParam("newName") String newName);

    default ResponseResult<Void> rename(String folderPath, String jobName, String newName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? renameRootJob(jobName, newName)
            : renameJobInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, newName);
        return ofVoid(response);
    }

    // below four apis are for "pipeline-stage-view-plugin",
    // see https://github.com/jenkinsci/pipeline-stage-view-plugin/tree/master/rest-api
    @GET
    @Path("job/{name}/wfapi/runs")
    @Consumes(MediaType.APPLICATION_JSON)
    Response rootRunHistory(@PathParam("name") String jobName);

    @GET
    @Path("/{folderPath}/job/{name}/wfapi/runs")
    @Consumes(MediaType.APPLICATION_JSON)
    Response runHistoryInFolder(@PathParam("folderPath") String folderPath,
                                @PathParam("name") String jobName);

    default ResponseResult<List<Workflow>> runHistory(String folderPath, String jobName)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? rootRunHistory(jobName)
            : runHistoryInFolder(appendFolderPrefixIfNeeded(folderPath), jobName);
        return of(response, (Class<List<Workflow>>) (Class<?>) List.class);
    }

    @GET
    @Path("job/{name}/{number}/wfapi/describe")
    @Consumes(MediaType.APPLICATION_JSON)
    Response rootWorkflow(@PathParam("name") String jobName, @PathParam("number") int buildNumber);

    @GET
    @Path("/{folderPath}/job/{name}/{number}/wfapi/describe")
    @Consumes(MediaType.APPLICATION_JSON)
    Response workflowInFolder(@PathParam("folderPath") String folderPath,
                              @PathParam("name") String jobName,
                              @PathParam("number") int buildNumber);

    default ResponseResult<Workflow> workflow(String folderPath, String jobName, int buildNumber)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? rootWorkflow(jobName, buildNumber)
            : workflowInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber);
        return of(response, Workflow.class);
    }


    @GET
    @Path("job/{name}/{number}/execution/node/{nodeId}/wfapi/describe")
    @Consumes(MediaType.APPLICATION_JSON)
    Response rootPipelineNode(@PathParam("name") String jobName,
                              @PathParam("number") int buildNumber,
                              @PathParam("nodeId") int nodeId);

    @GET
    @Path("/{folderPath}/job/{name}/{number}/execution/node/{nodeId}/wfapi/describe")
    @Consumes(MediaType.APPLICATION_JSON)
    Response pipelineNodeInFolder(@PathParam("folderPath") String folderPath,
                                  @PathParam("name") String jobName,
                                  @PathParam("number") int buildNumber,
                                  @PathParam("nodeId") int nodeId);

    default ResponseResult<PipelineNode> pipelineNode(String folderPath, String jobName, int buildNumber, int nodeId)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? rootPipelineNode(jobName, buildNumber, nodeId)
            : pipelineNodeInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber, nodeId);
        return of(response, PipelineNode.class);
    }

    @GET
    @Path("job/{name}/{number}/execution/node/{nodeId}/wfapi/log")
    @Consumes(MediaType.APPLICATION_JSON)
    Response rootPipelineNodeLog(@PathParam("name") String jobName,
                                 @PathParam("number") int buildNumber,
                                 @PathParam("nodeId") int nodeId);

    @GET
    @Path("/{folderPath}/job/{name}/{number}/execution/node/{nodeId}/wfapi/log")
    @Consumes(MediaType.APPLICATION_JSON)
    Response pipelineNodeLogInFolder(@PathParam("folderPath") String folderPath,
                                     @PathParam("name") String jobName,
                                     @PathParam("number") int buildNumber,
                                     @PathParam("nodeId") int nodeId);

    default ResponseResult<PipelineNodeLog> pipelineNodeLog(String folderPath,
                                                            String jobName,
                                                            int buildNumber,
                                                            int nodeId)
    {
        Response response = (folderPath == null || folderPath.isEmpty())
            ? rootPipelineNodeLog(jobName, buildNumber, nodeId)
            : pipelineNodeLogInFolder(appendFolderPrefixIfNeeded(folderPath), jobName, buildNumber, nodeId);
        return of(response, PipelineNodeLog.class);
    }
}
