package com.dlink.metadata;

import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

/**
 * MysqlTest
 *
 * @author wenmo
 * @since 2021/7/20 15:32
 **/
public class MysqlTest {

    private static final String IP = "127.0.0.1";

    public Driver getDriver() {
        DriverConfig config = new DriverConfig();
        config.setName(UUID.randomUUID().toString());
        config.setType("Mysql");
        config.setIp(IP);
        config.setPort(3306);
        config.setUsername("dca");
        config.setPassword("dca");
        config.setUrl("jdbc:mysql://" + IP + ":3306/dca?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&autoReconnect=true");
        return Driver.build(config);
    }

    @Test
    public void connectTest() {
        DriverConfig config = new DriverConfig();
        config.setType("Mysql");
        config.setIp(IP);
        config.setPort(3306);
        config.setUsername("dca");
        config.setPassword("dca");
        config.setUrl("jdbc:mysql://" + IP + ":3306/dca?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&autoReconnect=true");
        String test = Driver.build(config).test();
        System.out.println(test);
        System.out.println("end...");
    }

    @Test
    public void schemaTest() {
        Driver driver = getDriver();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        System.out.println("end...");
    }

    @Test
    public void columnTest() {
        Driver driver = getDriver();
        List<Column> columns = driver.listColumns("dca", "MENU");
        System.out.println("end...");
    }

    @Test
    public void queryTest() {
        Driver driver = getDriver();
        JdbcSelectResult query = driver.query("select * from MENU", 10);
        System.out.println("end...");
    }
}
