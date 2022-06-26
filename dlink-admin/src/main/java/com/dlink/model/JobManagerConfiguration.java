package com.dlink.model;

import lombok.Data;

import java.util.Map;

/**
 * @program: dlink
 * @description: JobManager 配置信息
 * @author: zhumingye
 * @create: 2022-06-26 10:53
 */

@Data
public class JobManagerConfiguration {

    private Map<String, String> metrics ;

    private Map<String, String> jobManagerConfig;

    private String jobManagerLog;

    private String jobManagerStdout;

}
