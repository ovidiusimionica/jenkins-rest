package com.cdancy.jenkins.rest.domain.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class PipelineNode {

    private final String name;
    private final String status;
    private final long startTimeMillis;
    private final long durationTimeMillis;
    private final List<StageFlowNode> stageFlowNodes;

    @JsonCreator
    public PipelineNode(
        @JsonProperty("name") String name,
        @JsonProperty("status") String status,
        @JsonProperty("startTimeMillis") long startTimeMillis,
        @JsonProperty("durationTimeMillis") long durationTimeMillis,
        @JsonProperty("stageFlowNodes") List<StageFlowNode> stageFlowNodes
    ) {
        this.name = name;
        this.status = status;
        this.startTimeMillis = startTimeMillis;
        this.durationTimeMillis = durationTimeMillis;
        this.stageFlowNodes = stageFlowNodes != null ? List.copyOf(stageFlowNodes) : Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getDurationTimeMillis() {
        return durationTimeMillis;
    }

    public List<StageFlowNode> getStageFlowNodes() {
        return stageFlowNodes;
    }
}
