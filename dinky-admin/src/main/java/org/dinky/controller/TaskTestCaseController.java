package org.dinky.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.annotations.TaskId;
import org.dinky.data.dto.TaskTestCaseListDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.result.Result;
import org.dinky.service.TaskTestCaseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "Task Test Case Controller")
@RequestMapping("/api/testCase")
@SaCheckLogin
@RequiredArgsConstructor
public class TaskTestCaseController {
    private final TaskTestCaseService taskTestCaseService;

    @PostMapping("/listTaskTestCaseByStatement")
    @ApiOperation("Query Test cases Bv TaskId")
    @ApiImplicitParam(
            name = "TaskTestcaseDTO",
            value = "Task Test case DTO",
            dataType = "TaskTestcaseDTO",
            paramType = "body",
            required = true
    )
    @CheckTaskOwner(checkParam = TaskId.class, checkInterface = TaskTestCaseService.class)
    public Result<TaskTestCaseListDTO> getTestCaseBasedOnStatement(@RequestBody TaskTestCaseListDTO taskTestCaseListDTO) {
        return Result.succeed(taskTestCaseService.getTestCaseBasedOnStatement(taskTestCaseListDTO));
    }

    @PostMapping("/saveDrUpdateTestCase")
    @ApiOperation("Save Or Update Test Cases By TaskId")
    @ApiImplicitParam(
            name = "taskTestCaseDTO",
            value = "task id and test cases of every source table",
            dataType = "TaskTestCaseDTO",
            paramType = "body",
            required = true
    )
    @CheckTaskOwner(checkParam = TaskId.class, checkInterface = TaskTestCaseService.class)
    public Result<Void> saveOrUpdateTestCase(@RequestBody TaskTestCaseListDTO taskTestCaseListDTO) {
        try {
            taskTestCaseService.savOrUpdateTestCase(taskTestCaseListDTO);
            return Result.succeed(Status.MODIFY_SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failed(Status.MODIFY_FAILED);

        }
    }
}


