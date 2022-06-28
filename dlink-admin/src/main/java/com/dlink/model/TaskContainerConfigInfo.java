package com.dlink.model;/**
 * @program: dlink
 * @description:
 * @author: zhumingye
 * @create: 2022-06-27 11:41
 */

import lombok.Data;

import java.util.Map;

/**
 * @program: dlink
 * @description:
 * @author: zhumingye
 * @create: 2022-06-27 11:41
 */

@Data
public class TaskContainerConfigInfo {

    private Map<String, String> metrics ;

    private String taskManagerLog;

    private String taskManagerStdout;

    private String taskManagerThreadDump;

}
