package com.dlink.cdc;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.mysql.MysqlCDCBuilder;
import com.dlink.exception.FlinkClientException;
import com.dlink.model.FlinkCDCConfig;

/**
 * CDCBuilderFactory
 *
 * @author wenmo
 * @since 2022/4/12 21:12
 **/
public class CDCBuilderFactory {

    private static CDCBuilder[] cdcBuilders = {
        new MysqlCDCBuilder()
    };

    public static CDCBuilder buildCDCBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getType())) {
            throw new FlinkClientException("请指定 CDC Source 类型。");
        }
        for (int i = 0; i < cdcBuilders.length; i++) {
            if (config.getType().equals(cdcBuilders[i].getHandle())) {
                return cdcBuilders[i].create(config);
            }
        }
        throw new FlinkClientException("未匹配到对应 CDC Source 类型的【" + config.getType() + "】。");
    }
}
