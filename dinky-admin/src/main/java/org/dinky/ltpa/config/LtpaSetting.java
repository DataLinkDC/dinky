package org.dinky.ltpa.config;

/**
 * Author: lwjhn
 * Date: 2023/7/3 15:08
 * Description:
 */
public class LtpaSetting {
    protected String secret = "2OqzZkZ//RvOLF+X1HqNJWcCBHE="; // 秘钥
    protected long expiration = 43200L;    // 默认过期-LTPA_Validity时间为43200秒(12小时)
    protected long transition = 300L;   // 过渡时间-LTPA_TokenExpiration.保证各服务时间不同步时的误差，过期后的这个时间内仍然有效，默认300秒(5分钟)
//    protected String version="00010203";  //LtpaToken 版本（长度4），Domino的固定为[0x00][0x01][0x02][0x03]

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getTransition() {
        return transition;
    }

    public void setTransition(long transition) {
        this.transition = transition;
    }

//    public String getVersion() {
//        return version;
//    }
//
//    public void setVersion(String version) {
//        this.version = version;
//    }
}
