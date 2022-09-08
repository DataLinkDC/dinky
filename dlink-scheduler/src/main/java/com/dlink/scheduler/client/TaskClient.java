package com.dlink.scheduler.client;

import com.dlink.scheduler.constant.Constants;
import com.dlink.scheduler.model.TaskMainInfo;
import com.dlink.scheduler.result.PageInfo;
import com.dlink.scheduler.utils.MyJSONUtil;
import com.dlink.scheduler.utils.ParamUtil;
import com.dlink.scheduler.utils.ReadFileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务定义
 *
 * @author 郑文豪
 */
@Component
@Slf4j
public class TaskClient {

    @Value("${dinky.dolphinscheduler.url}")
    private static String url;
    @Value("${dinky.dolphinscheduler.token}")
    private static String tokenKey;
    @Value("${dinky.url}")
    private static String dinkyUrl;

    /**
     * 查询任务定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @param taskName    任务定义名称
     * @return {@link TaskMainInfo}
     * @author 郑文豪
     * @date 2022/9/7 17:16
     */
    public TaskMainInfo getTaskDefinitionInfo(Long projectCode, String processName, String taskName) {
        List<TaskMainInfo> lists = getTaskDefinition(projectCode, processName, taskName);
        for (TaskMainInfo list : lists) {
            if (list.getTaskName().equalsIgnoreCase(taskName)) {
                return list;
            }
        }
        return null;
    }

    /**
     * 查询任务定义集合
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @param taskName    任务定义名称
     * @return {@link List<TaskMainInfo>}
     * @author 郑文豪
     * @date 2022/9/7 17:16
     */
    public List<TaskMainInfo> getTaskDefinition(Long projectCode, String processName, String taskName) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition", map);

        Map<String, Object> pageParams = ParamUtil.getPageParams();
        pageParams.put("searchTaskName", taskName);
        pageParams.put("searchWorkflowName", processName);
        pageParams.put("taskType", "DINKY");

        String content = HttpRequest.post(format)
            .header(Constants.TOKEN, tokenKey)
            .form(pageParams)
            .execute().body();

        PageInfo<JSONObject> data = MyJSONUtil.toPageBean(content);
        List<TaskMainInfo> lists = new ArrayList<>();
        if (data == null || data.getTotalList() == null) {
            return lists;
        }

        for (JSONObject jsonObject : data.getTotalList()) {
            if (processName.equalsIgnoreCase(jsonObject.getStr("processDefinitionName"))) {
                lists.add(MyJSONUtil.toBean(jsonObject, TaskMainInfo.class));
            }
        }
        return lists;
    }

    /**
     * 创建任务定义
     *
     * @param projectCode 项目编号
     * @param processCode 工作流定义编号
     * @param taskName    任务定义名称
     * @param dinkyTaskId dinky作业id
     * @return {@link TaskMainInfo}
     * @author 郑文豪
     * @date 2022/9/7 17:05
     */
    public TaskMainInfo createTaskDefinition(Long projectCode, Long processCode, String taskName,
                                             String dinkyTaskId) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/task-definition", map);

        Map<String, Object> pageParams = ParamUtil.getPageParams();
        pageParams.put("processDefinitionCode", processCode);

        // pageParams.put("upstreamCodes", definitionName);
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("code", "0");
        taskMap.put("name", taskName);
        taskMap.put("address", dinkyUrl);
        taskMap.put("taskId", dinkyTaskId);

        String taskDefinitionJson = ReadFileUtil.taskDefinition(taskMap);
        pageParams.put("taskDefinitionJsonObj", taskDefinitionJson);

        String content = HttpRequest.post(format)
            .header(Constants.TOKEN, tokenKey)
            .form(pageParams)
            .execute().body();

        return MyJSONUtil.toBean(content, TaskMainInfo.class);
    }

}
