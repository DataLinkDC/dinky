package com.dlink.parser;

/**
 * CreateAggTableSelectSqlParser
 *
 * @author wenmo
 * @since 2021/6/14 16:56
 */
public class CreateAggTableSelectSqlParser extends BaseSingleSqlParser {

    public CreateAggTableSelectSqlParser(String originalSql) {
        super(originalSql);
    }

    @Override
    protected void initializeSegments() {
        segments.add(new SqlSegment("(create\\s+aggtable)(.+)(as\\s+select)", "[,]"));
        segments.add(new SqlSegment("(select)(.+)(from)", "[,]"));
        segments.add(new SqlSegment("(from)(.+?)( where | on | having | group\\s+by | order\\s+by | agg\\s+by | ENDOFSQL)", "(,|\\s+left\\s+join\\s+|\\s+right\\s+join\\s+|\\s+inner\\s+join\\s+)"));
        segments.add(new SqlSegment("(where|on|having)(.+?)( group\\s+by | order\\s+by | agg\\s+by | ENDOFSQL)", "(and|or)"));
        segments.add(new SqlSegment("(group\\s+by)(.+?)( order\\s+by | agg\\s+by | ENDOFSQL)", "[,]"));
        segments.add(new SqlSegment("(order\\s+by)(.+?)( agg\\s+by | ENDOFSQL)", "[,]"));
        segments.add(new SqlSegment("(agg\\s+by)(.+?)( ENDOFSQL)", "[,]"));
    }
}
