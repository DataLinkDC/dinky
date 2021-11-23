package com.dlink.parser;

/**
 * InsertSqlParser
 *
 * @author wenmo
 * @since 2021/6/14 16:54
 */
public class InsertSqlParser extends BaseSingleSqlParser {

    public InsertSqlParser(String originalSql) {
        super(originalSql);
    }

    @Override
    protected void initializeSegments() {
        segments.add(new SqlSegment("(insert\\s+into)(.+?)([(])", "[,]"));
        segments.add(new SqlSegment("([(])(.+?)([)]\\s+values\\s+[(])", "[,]"));
        segments.add(new SqlSegment("([)]\\s+values\\s+[(])(.+)([)]\\s+ENDOFSQL)", "[,]"));
    }

    public String getParsedSql() {
        String retval = super.getParsedSql();
        retval = retval + ")";
        return retval;
    }
}

