package com.cdancy.jenkins.rest.domain.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PipelineNodeLog {

    private final String nodeId;
    private final String nodeStatus;
    private final int length;
    private final boolean hasMore;
    private final String text;
    private final String consoleUrl;

    @JsonCreator
    public PipelineNodeLog(
        @JsonProperty("nodeId") String nodeId,
        @JsonProperty("nodeStatus") String nodeStatus,
        @JsonProperty("length") int length,
        @JsonProperty("hasMore") boolean hasMore,
        @JsonProperty("text") String text,
        @JsonProperty("consoleUrl") String consoleUrl
    ) {
        this.nodeId = nodeId;
        this.nodeStatus = nodeStatus;
        this.length = length;
        this.hasMore = hasMore;
        this.text = text;
        this.consoleUrl = consoleUrl;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public int getLength() {
        return length;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public String getText() {
        return text;
    }

    public String getConsoleUrl() {
        return consoleUrl;
    }
}
