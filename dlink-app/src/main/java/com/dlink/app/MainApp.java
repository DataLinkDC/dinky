package com.dlink.app;

import com.dlink.app.db.DBConfig;
import com.dlink.app.flinksql.Submiter;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.utils.FlinkBaseUtil;

import java.io.IOException;
import java.util.Map;

/**
 * MainApp
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class MainApp {

    public static void main(String[] args) throws IOException {
        Map<String, String> params = FlinkBaseUtil.getParamsFromArgs(args);
        String id = params.get(FlinkParamConstant.ID);
        Asserts.checkNullString(id, "请配置入参 id ");
        DBConfig dbConfig = DBConfig.build(params);
        Submiter.submit(Integer.valueOf(id), dbConfig);
    }
}
