package com.dlink.metadata.driver;

import com.dlink.metadata.constant.PhoenixConstant;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.PhoenixTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.PhoenixQuery;
import com.dlink.model.Column;
import com.dlink.model.Table;
import java.util.List;

/**
 *
 * @author lcg
 * @operate 
 * @date 2022/2/16 16:50
 * @return 
 */
public class PhoenixDriver extends AbstractJdbcDriver {
    @Override
    public IDBQuery getDBQuery() {
        return new PhoenixQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new PhoenixTypeConvert();
    }

    @Override
    String getDriverClass() {
        return PhoenixConstant.PHOENIX_DRIVER;
    }

    @Override
    public String getType() {
        return "Phoenix";
    }

    @Override
    public String getName() {
        return "Phoenix";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sql = new StringBuilder();
        List<Column> columns = table.getColumns();
        sql.append(" CREATE VIEW IF NOT EXISTS \"" + table.getName() + "\" ( ");
        sql.append("    rowkey varchar primary key ");
        PhoenixTypeConvert phoenixTypeConvert = new PhoenixTypeConvert();
        if (columns != null) {
            for (Column column : columns) {
                sql.append(", \"" + column.getColumnFamily() + "\".\"" + column.getName() + "\"  " + phoenixTypeConvert.convertToDB(column));
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }
}
