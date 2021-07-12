package com.dlink.service;

import com.dlink.dto.SessionDTO;
import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.explainer.ca.ColumnCANode;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.job.JobResult;
import com.dlink.result.IResult;
import com.dlink.result.SelectResult;
import com.dlink.session.ExecutorEntity;
import com.dlink.session.SessionInfo;

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

    SelectResult getJobData(String jobId);

    SessionInfo createSession(SessionDTO sessionDTO, String createUser);

    boolean clearSession(String session);

    List<SessionInfo> listSession(String createUser);

    List<TableCANode> getOneTableCAByStatement(String statement);

    List<TableCANode> getOneTableColumnCAByStatement(String statement);

    List<ColumnCANode> getColumnCAByStatement(String statement);
}
