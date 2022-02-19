package com.dlink.parser;

/**
 * ShowFragmentsParser
 *
 * @author wenmo
 * @since 2022/2/17 16:19
 **/
public class ShowFragmentParser extends BaseSingleSqlParser {

    public ShowFragmentParser(String originalSql) {
        super(originalSql);
    }

    @Override
    protected void initializeSegments() {
        //SHOW FRAGMENT (.+)
        segments.add(new SqlSegment("FRAGMENT", "(show\\s+fragment)\\s+(.*)( ENDOFSQL)", ","));
    }
}
