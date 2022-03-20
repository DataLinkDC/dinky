package com.dlink.metadata.driver;

import com.dlink.metadata.constant.PhoenixConstant;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.PhoenixTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.PhoenixQuery;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
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

    @Override
    public Driver connect() {
        try {
            Class.forName(getDriverClass());
            //TODO：phoenix连接配置，后续可设置为参数传入，以适应不同配置的集群
            Properties properties = new Properties();
            properties.put("phoenix.schema.isNamespaceMappingEnabled", "true");
            properties.put("phoenix.schema.mapSystemTablesToNamespac", "true");
            conn = DriverManager.getConnection(config.getUrl(), properties);
            //设置为自动提交，否则upsert语句不生效
            conn.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * 解决phoenix SQL多语句执行问题
     * phoenix SQL中不能执行带;语句
     *
     * @param sql
     * @return
     */
    public String parsePhoenixSql(String sql) {
        return StringUtils.remove(sql, ";");
    }

    @Override
    public JdbcSelectResult query(String sql, Integer limit) {
        return super.query(parsePhoenixSql(sql), limit);
    }

    @Override
    public int executeUpdate(String sql) throws Exception {
        return super.executeUpdate(parsePhoenixSql(sql));
    }

    @Override
    public boolean execute(String sql) throws Exception {
        return super.execute(parsePhoenixSql(sql));
    }
}
