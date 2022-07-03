package com.dlink.controller;

import cn.hutool.core.bean.BeanUtil;
import com.dlink.common.result.ProTableResult;
import com.dlink.dto.TaskVersionHistoryDTO;
import com.dlink.model.TaskVersion;
import com.dlink.service.TaskVersionService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务版本 Controller
 *
 * @author wenmo
 * @since 2022-06-28
 */
@Slf4j
@RestController
@RequestMapping("/api/task/version")
public class TaskVersionController {
    @Autowired
    private TaskVersionService versionService;


    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<TaskVersionHistoryDTO> listTasks(@RequestBody JsonNode para) {
        ProTableResult<TaskVersionHistoryDTO> versionHistoryDTOProTableResult = new ProTableResult<>();

        ProTableResult<TaskVersion> versionProTableResult = versionService.selectForProTable(para);

        BeanUtil.copyProperties(versionProTableResult, versionHistoryDTOProTableResult);
        List<TaskVersionHistoryDTO> collect = versionProTableResult.getData().stream().map(t -> {
            TaskVersionHistoryDTO versionHistoryDTO = new TaskVersionHistoryDTO();
            versionHistoryDTO.setVersionId(t.getVersionId());
            versionHistoryDTO.setId(t.getId());
            versionHistoryDTO.setCreateTime(t.getCreateTime());
            return versionHistoryDTO;
        }).collect(Collectors.toList());

        versionHistoryDTOProTableResult.setData(collect);

        return versionHistoryDTOProTableResult;
    }

}

