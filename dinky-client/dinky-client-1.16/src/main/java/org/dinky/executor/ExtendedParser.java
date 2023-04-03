package org.dinky.executor;

import org.apache.flink.table.delegation.Parser;

/**
 *
 */
public interface ExtendedParser extends Parser {
    CustomParser getCustomParser();
}
