package com.dlink.parser;

/**
 * CreateCDCSourceSqlParser
 *
 * @author wenmo
 * @since 2022/1/29 23:39
 */
public class CreateCDCSourceSqlParser extends BaseSingleSqlParser {

    public CreateCDCSourceSqlParser(String originalSql) {
        super(originalSql);
    }

    @Override
    protected void initializeSegments() {
        segments.add(new SqlSegment("CDCSOURCE", "(execute\\s+cdcsource\\s+)(.+)(\\s+with\\s+\\()", "[,]"));
        segments.add(new SqlSegment("WITH", "(with\\s+\\()(.+)(\\))", "[,]"));
    }
}
