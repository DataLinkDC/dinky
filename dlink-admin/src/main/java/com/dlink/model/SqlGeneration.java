package com.dlink.model;

/**
 * SqlGeneration
 *
 * @author wenmo
 * @since 2022/1/29 16:13
 */
public class SqlGeneration {
    private String flinkSqlCreate;
    private String sqlSelect;
    private String sqlCreate;

    public SqlGeneration() {
    }

    public SqlGeneration(String flinkSqlCreate, String sqlSelect, String sqlCreate) {
        this.flinkSqlCreate = flinkSqlCreate;
        this.sqlSelect = sqlSelect;
        this.sqlCreate = sqlCreate;
    }

    public String getFlinkSqlCreate() {
        return flinkSqlCreate;
    }

    public void setFlinkSqlCreate(String flinkSqlCreate) {
        this.flinkSqlCreate = flinkSqlCreate;
    }

    public String getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(String sqlSelect) {
        this.sqlSelect = sqlSelect;
    }

    public String getSqlCreate() {
        return sqlCreate;
    }

    public void setSqlCreate(String sqlCreate) {
        this.sqlCreate = sqlCreate;
    }
}
