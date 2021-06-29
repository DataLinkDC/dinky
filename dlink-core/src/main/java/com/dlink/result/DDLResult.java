package com.dlink.result;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DDLResult
 *
 * @author wenmo
 * @since 2021/6/29 22:06
 */
@Setter
@Getter
public class DDLResult extends AbstractResult implements IResult {

    public DDLResult(boolean success) {
        this.success = success;
        this.endTime = LocalDateTime.now();
    }
}
