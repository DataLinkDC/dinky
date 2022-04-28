package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class SqlServerTest {

    private Driver driver;

    @Before
    public void init() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("SqlServer");
        config.setIp("192.168.68.133");
        config.setPort(1433);
        config.setUsername("sa");
        config.setPassword("OcP2020123");
        config.setUrl("jdbc:sqlserver://192.168.68.133:1433;DatabaseName=test");
        try {
            driver = Driver.build(config);
        } catch (Exception e) {
            System.err.println("连接创建失败:" + e.getMessage());
        }
    }

    @Test
    public void test() throws SQLException {
        //test
        String test = driver.test();
        System.out.println(test);
        System.out.println("schema && table...");
        testSchema();
        System.out.println("columns...");
        testColumns();
        System.out.println("query...");
        query();
    }

    @Test
    public void testSchema() {
        //schema && table
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        for (Schema schemasAndTable : schemasAndTables) {
            List<Table> tables = schemasAndTable.getTables();
            for (Table table : tables) {
                System.out.println(table.getName() + "  " + table.getSchema());
            }
        }
    }

    @Test
    public void testColumns() {
        // columns
        List<Column> columns = driver.listColumns("dbo", "t_user");
        for (Column column : columns) {
            System.out.println(column.getName() + " " + column.getType() + " " + column.getComment());
        }
    }

    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from t_user", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            System.out.println(rowDatum);
        }
    }


}
