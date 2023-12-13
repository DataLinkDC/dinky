package org.dinky.operations;

import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.planner.parse.ExtendedParser;
import org.dinky.parser.CustomParserImpl;

public class CustomNewParserImpl extends CustomParserImpl {

    private final DinkyParser dinkyParser;

    public CustomNewParserImpl(TableEnvironment tableEnvironment, Parser parser) {
        super(parser);
        this.dinkyParser = new DinkyParser(tableEnvironment);
    }

    @Override
    public ExtendedParser getDinkyParser() {
        return this.dinkyParser;
    }

}
