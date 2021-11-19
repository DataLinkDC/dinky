package com.dlink.app;

import com.dlink.app.db.DBConfig;
import com.dlink.app.flinksql.Submiter;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * MainApp
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class MainApp {

    public static void main(String[] args) throws IOException {
        ParameterTool parameters = ParameterTool.fromArgs(args);
        String id = parameters.get("id", null);
        if (id!=null&&!"".equals(id)) {
            DBConfig dbConfig = DBConfig.build(parameters);
            Submiter.submit(Integer.valueOf(id),dbConfig);
        }
    }
}
