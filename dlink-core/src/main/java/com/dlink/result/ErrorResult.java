package com.dlink.result;

import java.time.LocalDateTime;

/**
 * ErrorResult
 *
 * @author wenmo
 * @since 2021/6/29 22:57
 */
public class ErrorResult extends AbstractResult implements IResult {

    public ErrorResult() {
        this.success = false;
        this.endTime = LocalDateTime.now();
    }
}
