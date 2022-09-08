package com.dlink.scheduler.client;

import cn.hutool.http.HttpRequest;
import com.dlink.scheduler.constant.Constants;
import com.dlink.scheduler.model.Project;
import com.dlink.scheduler.utils.MyJSONUtil;
import com.dlink.scheduler.utils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目
 *
 * @author 郑文豪
 * @date 2022/9/8 9:06
 */
@Component
@Slf4j
public class ProjectClient {

    @Value("${dinky.dolphinscheduler.url}")
    private static String url;
    @Value("${dinky.dolphinscheduler.token}")
    private static String tokenKey;
    @Value("${dinky.dolphinscheduler.project-name}")
    private static String dinkyProjectName;


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
                .execute().body();
        return MyJSONUtil.toBean(content, Project.class);
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
                .execute().body();

        return MyJSONUtil.toPageBeanAndFindByName(content, dinkyProjectName, Project.class);
    }
}
