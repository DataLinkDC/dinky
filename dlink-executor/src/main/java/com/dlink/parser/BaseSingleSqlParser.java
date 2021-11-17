package com.dlink.parser;

import com.dlink.assertion.Asserts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BaseSingleSqlParser
 *
 * @author wenmo
 * @since 2021/6/14 16:43
 */
public abstract class BaseSingleSqlParser {

    //原始Sql语句
    protected String originalSql;

    //Sql语句片段
    protected List<SqlSegment> segments;


    /**
     * 构造函数，传入原始Sql语句，进行劈分。
     **/
    public BaseSingleSqlParser(String originalSql) {
        this.originalSql = originalSql;
        segments = new ArrayList<SqlSegment>();
        initializeSegments();
    }

    /**
     * 初始化segments，强制子类实现
     **/
    protected abstract void initializeSegments();

    /**
     * 将originalSql劈分成一个个片段
     **/
    protected Map<String,List<String>> splitSql2Segment() {
        Map<String,List<String>> map = new HashMap<>();
        for (SqlSegment sqlSegment : segments) {
            sqlSegment.parse(originalSql);
            if(Asserts.isNotNullString(sqlSegment.getStart())) {
                map.put(sqlSegment.getStart().toUpperCase(), sqlSegment.getBodyPieces());
            }
        }
        return map;
    }

    /**
     * 得到解析完毕的Sql语句
     **/
    public String getParsedSql() {
        StringBuffer sb = new StringBuffer();
        for (SqlSegment sqlSegment : segments) {
            sb.append(sqlSegment.getParsedSqlSegment() + "\n");
        }
        String retval = sb.toString().replaceAll("\n+", "\n");
        return retval;
    }

}
