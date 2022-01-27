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

public class DorisTest {
    private Driver driver;
    @Before
    public void init(){
        DriverConfig config = new DriverConfig();
        config.setType("Doris");
        config.setIp("10.1.51.26");
        config.setPort(9030);
        config.setUsername("root");
        config.setPassword("dw123456");
        config.setUrl("jdbc:mysql://10.1.51.26:9030/test");
        try {
            driver =  Driver.build(config).connect();
        }catch (Exception e){
            System.err.println("连接创建失败");
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws SQLException {
        //test
        String test = driver.test();
        System.out.println(test);
        System.out.println("schema && table -----");
        testSchema();
        System.out.println("columns -----");
        testColumns();
        System.out.println("query -----");
        query();
    }

    @Test
    public void testSchema(){
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
    public void testColumns(){
        // columns
        List<Column> columns = driver.listColumns("test", "scoreinfo");
        for (Column column : columns) {
            System.out.println(column.getName() + " " + column.getType() + column.getComment() );
        }
    }
    @Test
    public void query(){
        JdbcSelectResult selectResult = driver.query("select * from scoreinfo ", 10);
        List<LinkedHashMap<String, Object>> rowData = selectResult.getRowData();
        for (LinkedHashMap<String, Object> rowDatum : rowData) {
            System.out.println(rowData);
        }
    }
}
