package com.dlink.gateway.result;

import java.util.List;

/**
 * GatewayResult
 *
 * @author wenmo
 * @since 2021/10/29 15:39
 **/
public interface GatewayResult {

    String getAppId();

    String getWebURL();

    List<String> getJids();
}
