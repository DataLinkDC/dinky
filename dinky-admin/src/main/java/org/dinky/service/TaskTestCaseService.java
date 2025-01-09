package org.dinky.service;

import org.dinky.data.dto.TaskTestCaseListDTO;
import org.dinky.data.model.TableTestCase;
import org.dinky.mybatis.service.ISuperService;

public interface TaskTestCaseService extends ISuperService<TableTestCase> {

    TaskTestCaseListDTO getTestCaseBasedOnStatement(TaskTestCaseListDTO taskTestCaseListDTO);

    void savOrUpdateTestCase(TaskTestCaseListDTO taskTestCaseListDTO);

    Boolean checkTaskOperatePermission(Integer taskId);

    TableTestCase getTestCaseByTaskIdAndTableName(Integer taskId, String tableName);
}
