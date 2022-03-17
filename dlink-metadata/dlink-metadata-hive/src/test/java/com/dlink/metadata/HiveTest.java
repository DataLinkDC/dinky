package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.junit.Test;

import java.util.List;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 **/
public class HiveTest {

    private static final String IP = "cdh1";
    private static final Integer PORT = 10000;
    private static final String hiveDB = "test";
    private static final String username = "zhumingye";
    private static final String passwd = "123456";
    private static final String hive="Hive";

    private static String url = "jdbc:hive2://"+IP+":"+PORT+"/"+hiveDB;

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setType(hive);
        config.setName(hive);
        config.setIp(IP);
        config.setPort(PORT);
        config.setUsername(username);
        config.setPassword(passwd);
        config.setUrl(url);
        return Driver.build(config);
    }

    @Test
    public void connectTest() {
        DriverConfig config = new DriverConfig();
        config.setType(hive);
        config.setName(hive);
        config.setIp(IP);
        config.setPort(PORT);
        config.setUsername(username);
        config.setPassword(passwd);
        config.setUrl(url);
        String test = Driver.build(config).test();
        System.out.println(test);
        System.err.println("end...");
    }

    @Test
    public void getDBSTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.listSchemas();
        schemasAndTables.forEach(schema -> {
            System.out.println(schema.getName()+"\t\t"+schema.getTables().toString());
        });
        System.err.println("end...");
    }



    @Test
    public void getTablesByDBTest() throws Exception {
        Driver driver = getDriver();
        driver.execute("use odsp ");
        List<Table> tableList = driver.listTables(hiveDB);
        tableList.forEach(schema -> {
            System.out.println(schema.getName());
        });
        System.err.println("end...");
    }

    @Test
    public void getColumnsByTableTest() {
        Driver driver = getDriver();
        List<Column> columns= driver.listColumns(hiveDB, "biz_college_planner_mysql_language_score_item");
        for (Column column : columns) {
            System.out.println(column.getName()+" \t "+column.getType()+" \t "+column.getComment());
        }
        System.err.println("end...");
    }

    @Test
    public void getCreateTableTest() throws Exception {
        Driver driver = getDriver();
//        JdbcSelectResult jdbcSelectResult = driver.executeSql("show create table odsp.biz_college_planner_mysql_language_score_item", 1);
        Table driverTable = driver.getTable(hiveDB, "biz_college_planner_mysql_language_score_item");
        String createTableSql = driver.getCreateTableSql(driverTable);
        System.out.println(createTableSql);
        System.err.println("end...");
    }


    @Test
    public void getTableExtenedInfoTest() throws Exception {
        Driver driver = getDriver();
        Table driverTable = driver.getTable(hiveDB, "employees");
        for (Column column : driverTable.getColumns()) {
            System.out.println(column.getName()+"\t\t"+column.getType()+"\t\t"+column.getComment());
        }
    }
}
