package com.dlink.service;

import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.explainer.ca.ColumnCANode;
import com.dlink.explainer.ca.TableCANode;
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

    IResult executeDDL(StudioDDLDTO studioDDLDTO);

    List<SqlExplainResult> explainSql(StudioExecuteDTO studioExecuteDTO);

    ObjectNode getStreamGraph(StudioExecuteDTO studioExecuteDTO);

    ObjectNode getJobPlan(StudioExecuteDTO studioExecuteDTO);

    SelectResult getJobData(String jobId);

    SessionInfo createSession(SessionDTO sessionDTO, String createUser);

    boolean clearSession(String session);

    List<SessionInfo> listSession(String createUser);

    List<TableCANode> getOneTableCAByStatement(String statement);

    List<TableCANode> getOneTableColumnCAByStatement(String statement);

    List<ColumnCANode> getColumnCAByStatement(String statement);

    List<JsonNode> listJobs(Integer clusterId);

    boolean cancel(Integer clusterId,String jobId);

    boolean savepoint(Integer clusterId,String jobId,String savePointType,String name);
}
