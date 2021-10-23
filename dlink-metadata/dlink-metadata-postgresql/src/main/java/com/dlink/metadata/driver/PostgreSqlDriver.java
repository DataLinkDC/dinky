package com.dlink.metadata.driver;

import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.PostgreSqlTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.PostgreSqlQuery;
import com.dlink.model.Table;

/**
 * PostgreSqlDriver
 *
 * @author wenmo
 * @since 2021/7/22 9:28
 **/
public class PostgreSqlDriver extends AbstractJdbcDriver {
    @Override
    String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new PostgreSqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new PostgreSqlTypeConvert();
    }

    @Override
    public String getType() {
        return "PostgreSql";
    }

    @Override
    public String getName() {
        return "PostgreSql 数据库";
    }

    @Override
    public String getCreateTableSql(Table table) {
        return null;
    }
}
