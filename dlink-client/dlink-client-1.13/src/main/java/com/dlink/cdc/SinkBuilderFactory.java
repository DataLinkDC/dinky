package com.dlink.cdc;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.doris.DorisSinkBuilder;
import com.dlink.cdc.hudi.HudiSinkBuilder;
import com.dlink.cdc.jdbc.JdbcSinkBuilder;
import com.dlink.cdc.kafka.KafkaSinkBuilder;
import com.dlink.exception.FlinkClientException;
import com.dlink.model.FlinkCDCConfig;

/**
 * SinkBuilderFactory
 *
 * @author wenmo
 * @since 2022/4/12 21:12
 **/
public class SinkBuilderFactory {

    private static SinkBuilder[] sinkBuilders = {
        new KafkaSinkBuilder(),
        new JdbcSinkBuilder(),
        new DorisSinkBuilder(),
        new HudiSinkBuilder(),
    };

    public static SinkBuilder buildSinkBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getSink().get("connector"))) {
            throw new FlinkClientException("请指定 Sink connector。");
        }
        for (int i = 0; i < sinkBuilders.length; i++) {
            if (config.getSink().get("connector").equals(sinkBuilders[i].getHandle())) {
                return sinkBuilders[i].create(config);
            }
        }
        throw new FlinkClientException("未匹配到对应 Sink 类型的【" + config.getSink().get("connector") + "】。");
    }
}
