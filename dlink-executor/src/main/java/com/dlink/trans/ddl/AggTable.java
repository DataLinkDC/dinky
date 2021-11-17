package com.dlink.trans.ddl;

import com.dlink.parser.SingleSqlParserFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * AggTable
 *
 * @author wenmo
 * @since 2021/6/13 20:32
 */
public class AggTable {
    private String statement;
    private String name;
    private String columns;
    private String table;
    private List<String> wheres;
    private String groupBy;
    private String aggBy;

    public AggTable(String statement, String name, String columns, String table, List<String> wheres, String groupBy, String aggBy) {
        this.statement = statement;
        this.name = name;
        this.columns = columns;
        this.table = table;
        this.wheres = wheres;
        this.groupBy = groupBy;
        this.aggBy = aggBy;
    }

    public static AggTable build(String statement){
        Map<String,List<String>> map = SingleSqlParserFactory.generateParser(statement);
        return new AggTable(statement,
                getString(map,"CREATE AGGTABLE"),
                getString(map,"SELECT"),
                getString(map,"FROM"),
                map.get("WHERE"),
                getString(map,"GROUP BY"),
                getString(map,"AGG BY"));
    }

    private static String getString(Map<String,List<String>> map,String key){
        return StringUtils.join(map.get(key),",");
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getWheres() {
        return wheres;
    }

    public void setWheres(List<String> wheres) {
        this.wheres = wheres;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getAggBy() {
        return aggBy;
    }

    public void setAggBy(String aggBy) {
        this.aggBy = aggBy;
    }
}
