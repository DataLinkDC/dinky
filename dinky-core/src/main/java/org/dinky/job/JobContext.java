package org.dinky.job;

import lombok.Data;
import org.dinky.data.enums.GatewayType;
import org.dinky.executor.Executor;
import org.dinky.executor.ExecutorConfig;

@Data
public class JobContext {
    private JobHandler handler;
    private ExecutorConfig executorConfig;
    private JobConfig config;
    private Executor executor;
    private boolean useGateway = false;
    private boolean isPlanMode = false;
    private boolean useStatementSet = false;
    private boolean useRestAPI = false;
    private GatewayType runMode = GatewayType.LOCAL;

    private JobParam jobParam = null;
    private Job job;
}
