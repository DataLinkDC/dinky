package com.dlink.result;

import org.apache.flink.table.api.TableResult;

/**
 * DDLResultBuilder
 *
 * @author wenmo
 * @since 2021/6/29 22:43
 */
public class DDLResultBuilder implements ResultBuilder {
    @Override
    public IResult getResult(TableResult tableResult) {
        return new DDLResult(true);
    }
}
