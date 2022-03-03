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

public class PhoenixTest {

    private Driver driver;

    @Before
    public void init() {
        DriverConfig config = new DriverConfig();
        config.setType("Phoenix");
        config.setIp("192.168.10.13");
        config.setPort(1433);
        config.setUsername("test");
        config.setPassword("test");
        config.setUrl("jdbc:phoenix:hd01,hd02,hd03:2181");
        try {
            driver = Driver.build(config).connect();
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
        System.out.println("query...");
        query();

        testSchema();
        System.out.println("columns...");
        testColumns();

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
        List<Column> columns = driver.listColumns("TEST", "ECGBEATS13");
        for (Column column : columns) {
            System.out.println(column.getName() + " " + column.isKeyFlag() + " " + column.getComment());
        }
    }

    @Test
    public void query() {
        JdbcSelectResult selectResult = driver.query("select * from test.ecgbeats12", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            System.out.println(rowData);
        }
    }


}
