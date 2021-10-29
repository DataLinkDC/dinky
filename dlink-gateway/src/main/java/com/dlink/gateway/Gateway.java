package com.dlink.gateway;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import org.apache.flink.runtime.jobgraph.JobGraph;
import sun.misc.Service;

import java.util.Iterator;
import java.util.Optional;

/**
 * Submiter
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public interface Gateway {

    static Optional<Gateway> get(GatewayConfig config){
        Asserts.checkNotNull(config,"配置不能为空");
        Iterator<Gateway> providers = Service.providers(Gateway.class);
        while(providers.hasNext()) {
            Gateway gateway = providers.next();
            if(gateway.canHandle(config.getType())){
                gateway.setGatewayConfig(config);
                return Optional.of(gateway);
            }
        }
        return Optional.empty();
    }

    static Gateway build(GatewayConfig config){
        Optional<Gateway> optionalGateway = Gateway.get(config);
        if(!optionalGateway.isPresent()){
            throw new GatewayException("不支持 Flink Gateway 类型【"+config.getType().getLongValue()+"】,请添加扩展包");
        }
        return optionalGateway.get();
    }

    boolean canHandle(GatewayType type);

    GatewayType getType();

    void setGatewayConfig(GatewayConfig config);

    GatewayResult submitJobGraph(JobGraph jobGraph);

    GatewayResult submitJar();

}
