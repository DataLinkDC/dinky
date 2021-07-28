package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkConstant;
import com.dlink.constant.NetConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * EnvironmentSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:45
 **/
@Getter
@Setter
public class EnvironmentSetting {
    private String host;
    private int port;
    private boolean useRemote;
    public static final EnvironmentSetting LOCAL = new EnvironmentSetting(false);

    public EnvironmentSetting(boolean useRemote) {
        this.useRemote = useRemote;
    }

    public EnvironmentSetting(String host, int port) {
        this.host = host;
        this.port = port;
        this.useRemote = true;
    }

    public static EnvironmentSetting build(String address){
        Asserts.checkNull(address,"Flink 地址不能为空");
        String[] strs = address.split(NetConstant.COLON);
        if (strs.length >= 2) {
            return new EnvironmentSetting(strs[0],Integer.parseInt(strs[1]));
        } else {
            return new EnvironmentSetting(strs[0],FlinkConstant.PORT);
        }
    }

    public String getAddress(){
        return host + NetConstant.COLON + port;
    }

}
