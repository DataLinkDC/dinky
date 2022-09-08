package com.dlink.scheduler.client;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.dlink.scheduler.constant.Constants;
import com.dlink.scheduler.model.ProcessDefinition;
import com.dlink.scheduler.result.PageInfo;
import com.dlink.scheduler.utils.MyJSONUtil;
import com.dlink.scheduler.utils.ParamUtil;
import com.dlink.scheduler.utils.ReadFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流定义
 *
 * @author 郑文豪
 * @date 2022/9/8 9:06
 */
@Component
@Slf4j
public class ProcessClient {

    @Value("${dinky.dolphinscheduler.url}")
    private static String url;
    @Value("${dinky.dolphinscheduler.token}")
    private static String tokenKey;
    @Value("${dinky.url}")
    private static String dinkyUrl;

    /**
     * 查询工作流定义
     *
     * @param projectCode 项目编号
     * @param name        工作流定义名
     * @return {@link   List<ProcessDefinition>}
     * @author 郑文豪
     * @date 2022/9/7 16:59
     */
    public List<ProcessDefinition> getProcessDefinition(Long projectCode, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/process-definition", map);

        String content = HttpRequest.get(format)
                .header(Constants.TOKEN, tokenKey)
                .form(ParamUtil.getPageParams(name))
                .execute().body();
        PageInfo<JSONObject> data = MyJSONUtil.toPageBean(content);
        List<ProcessDefinition> lists = new ArrayList<>();
        if (data == null || data.getTotalList() == null) {
            return lists;
        }

        for (JSONObject jsonObject : data.getTotalList()) {
            lists.add(MyJSONUtil.toBean(jsonObject, ProcessDefinition.class));
        }
        return lists;
    }

    /**
     * 查询工作流定义
     *
     * @param projectCode 项目编号
     * @param name        工作流定义名
     * @return {@link ProcessDefinition}
     * @author 郑文豪
     * @date 2022/9/7 16:59
     */
    public ProcessDefinition getProcessDefinitionInfo(Long projectCode, String name) {

        List<ProcessDefinition> lists = getProcessDefinition(projectCode, name);
        for (ProcessDefinition list : lists) {
            if (list.getName().equalsIgnoreCase(name)) {
                return list;
            }
        }
        return null;
    }

    /**
     * 创建工作流定义
     *
     * @param projectCode 项目编号
     * @param processName 工作流定义名称
     * @param taskName    任务定义名称
     * @param dinkyTaskId dinky作业id
     * @return {@link ProcessDefinition}
     * @author 郑文豪
     * @date 2022/9/7 17:00
     */
    public ProcessDefinition createProcessDefinition(Long projectCode, String processName,
                                                     String taskName, String dinkyTaskId) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectCode", projectCode);
        String format = StrUtil.format(url + "/projects/{projectCode}/process-definition", map);

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("code", IdUtil.getSnowflakeNextId());
        taskMap.put("name", taskName);
        taskMap.put("address", dinkyUrl);
        taskMap.put("taskId", dinkyTaskId);

        String taskDefinitionJson = ReadFileUtil.taskDefinition(taskMap);

        String taskRelationJson = ReadFileUtil.taskRelation(taskMap);

        Map<String, Object> params = new HashMap<>();
        params.put("name", processName);
        params.put("description", "系统添加");
        params.put("tenantCode", "default");
        params.put("taskRelationJson", taskRelationJson);
        params.put("taskDefinitionJson", taskDefinitionJson);
        params.put("executionType", "PARALLEL");

        String content = HttpRequest.post(format)
                .header(Constants.TOKEN, tokenKey)
                .form(params)
                .execute().body();

        return MyJSONUtil.toBean(content, ProcessDefinition.class);
    }

}
