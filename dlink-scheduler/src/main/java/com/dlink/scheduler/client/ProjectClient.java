package com.dlink.scheduler.client;

import com.dlink.scheduler.constant.Constants;
import com.dlink.scheduler.model.Project;
import com.dlink.scheduler.result.Result;
import com.dlink.scheduler.utils.MyJSONUtil;
import com.dlink.scheduler.utils.ParamUtil;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;

/**
 * 项目
 *
 * @author 郑文豪
 */
@Component
public class ProjectClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);

    @Value("${dinky.dolphinscheduler.url}")
    private String url;
    @Value("${dinky.dolphinscheduler.token}")
    private String tokenKey;
    @Value("${dinky.dolphinscheduler.project-name}")
    private String dinkyProjectName;

    /**
     * 创建项目
     *
     * @return {@link Project}
     * @author 郑文豪
     * @date 2022/9/7 16:57
     */
    public Project createDinkyProject() {
        Map<String, Object> map = new HashMap<>();
        map.put("projectName", dinkyProjectName);
        map.put("description", "自动创建");

        String content = HttpRequest.post(url + "/projects")
            .header(Constants.TOKEN, tokenKey)
            .form(map)
            .timeout(5000)
            .execute().body();
        return MyJSONUtil.verifyResult(MyJSONUtil.toBean(content, new TypeReference<Result<Project>>() {
        }));
    }

    /**
     * 查询项目
     *
     * @return {@link Project}
     * @author 郑文豪
     * @date 2022/9/7 16:57
     */
    public Project getDinkyProject() {

        String content = HttpRequest.get(url + "/projects")
            .header(Constants.TOKEN, tokenKey)
            .form(ParamUtil.getPageParams(dinkyProjectName))
            .timeout(5000)
            .execute().body();

        return MyJSONUtil.toPageBeanAndFindByName(content, dinkyProjectName, Project.class);
    }
}
