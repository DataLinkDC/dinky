package com.dlink.service;

import com.dlink.dto.*;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.job.JobResult;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.session.SessionInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * StudioService
 *
 * @author wenmo
 * @since 2021/5/30 11:07
 */
public interface StudioService {

    JobResult executeSql(StudioExecuteDTO studioExecuteDTO);

    JobResult executeCommonSql(SqlDTO sqlDTO);

    IResult executeDDL(StudioDDLDTO studioDDLDTO);

    List<SqlExplainResult> explainSql(StudioExecuteDTO studioExecuteDTO);

    ObjectNode getStreamGraph(StudioExecuteDTO studioExecuteDTO);

    ObjectNode getJobPlan(StudioExecuteDTO studioExecuteDTO);

    SelectResult getJobData(String jobId);

    SessionInfo createSession(SessionDTO sessionDTO, String createUser);

    boolean clearSession(String session);

    List<SessionInfo> listSession(String createUser);

    LineageResult getLineage(StudioCADTO studioCADTO);

    List<JsonNode> listJobs(Integer clusterId);

    boolean cancel(Integer clusterId, String jobId);

    boolean savepoint(Integer taskId, Integer clusterId, String jobId, String savePointType, String name);
}
