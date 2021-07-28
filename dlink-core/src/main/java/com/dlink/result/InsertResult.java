package com.dlink.result;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * InsertResult
 *
 * @author wenmo
 * @since 2021/5/25 19:08
 **/
@Getter
@Setter
public class InsertResult extends AbstractResult implements IResult {

    private String jobID;

    public InsertResult(String jobID, boolean success) {
        this.jobID = jobID;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String getJobId() {
        return jobID;
    }
}
