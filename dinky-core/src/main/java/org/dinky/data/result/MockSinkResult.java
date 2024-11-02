package org.dinky.data.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class MockSinkResult extends AbstractResult implements IResult {

    private String taskId;
    private Map<String, List<Map<String, String>>> tableRowData;
    private boolean truncationFlag = false;
    private boolean isDestroyed;

    public MockSinkResult(
            String taskId,
            Map<String, List<Map<String, String>>> tableRowData) {
        this.taskId = taskId;
        this.tableRowData = tableRowData;
    }

    public MockSinkResult(String taskId, boolean isDestroyed, boolean success) {
        this.taskId = taskId;
        this.isDestroyed = isDestroyed;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    public static MockSinkResult buildSuccess(String taskId) {
        return new MockSinkResult(taskId, false, true);
    }

    public static MockSinkResult buildFailed() {
        return new MockSinkResult(null, false, false);
    }

    @Override
    public String getJobId() {
        return this.taskId;
    }

}
