package com.dlink.model;

import lombok.Data;

/**
 * @program: dlink
 * @description:
 * @author: zhumingye
 * @create: 2022-06-27 11:18
 */

@Data
public class TaskManagerConfiguration {

    private String containerId;
    private String containerPath;
    private Integer dataPort;
    private Integer jmxPort;
    private Long timeSinceLastHeartbeat;
    private Integer slotsNumber;
    private Integer freeSlots;

    private String totalResource;
    private String freeResource;
    private String hardware;
    private String memoryConfiguration;

    private TaskContainerConfigInfo taskContainerConfigInfo;

}
