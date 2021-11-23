package com.dlink.parser;

/**
 * SelectSqlParser
 *
 * @author wenmo
 * @since 2021/6/14 16:53
 */
public class SelectSqlParser extends BaseSingleSqlParser {

    public SelectSqlParser(String originalSql) {
        super(originalSql);
    }

    @Override
    protected void initializeSegments() {
        segments.add(new SqlSegment("(select)(.+)(from)", "[,]"));
        segments.add(new SqlSegment("(from)(.+?)(where |group\\s+by|having|order\\s+by | ENDOFSQL)", "(,|s+lefts+joins+|s+rights+joins+|s+inners+joins+)"));
        segments.add(new SqlSegment("(where)(.+?)(group\\s+by |having| order\\s+by | ENDOFSQL)", "(and|or)"));
        segments.add(new SqlSegment("(group\\s+by)(.+?)(having|order\\s+by| ENDOFSQL)", "[,]"));
        segments.add(new SqlSegment("(having)(.+?)(order\\s+by| ENDOFSQL)", "(and|or)"));
        segments.add(new SqlSegment("(order\\s+by)(.+)( ENDOFSQL)", "[,]"));
    }
}

