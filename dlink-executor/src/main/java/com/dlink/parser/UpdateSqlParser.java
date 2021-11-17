package com.dlink.parser;

/**
 * UpdateSqlParser
 *
 * @author wenmo
 * @since 2021/6/14 16:52
 */
public class UpdateSqlParser extends BaseSingleSqlParser {

    public UpdateSqlParser(String originalSql) {
        super(originalSql);
    }

    @Override
    protected void initializeSegments() {
        segments.add(new SqlSegment("(update)(.+)(set)", "[,]"));
        segments.add(new SqlSegment("(set)(.+?)( where | ENDOFSQL)", "[,]"));
        segments.add(new SqlSegment("(where)(.+)(ENDOFSQL)", "(and|or)"));
    }

}

