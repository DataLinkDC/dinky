package com.dlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Dlink 启动器
 * @author  wenmo
 * @since  2021/5/28
 */
@EnableTransactionManagement
@SpringBootApplication
public class Dlink {

    public static void main(String[] args) {
        SpringApplication.run(Dlink.class, args);
    }
}
