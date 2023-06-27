package org.dinky.data.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
public class OssProperties {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "oss";

    /**
     * 是否启用 oss，默认为：true
     */
    private boolean enable = true;

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String customDomain;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false
     * supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style
     * 模式{http://bucketname.endpoint}
     */
    private Boolean pathStyleAccess = true;

    /**
     * 区域
     */
    private String region;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;

    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    /**
     * 默认的存储桶名称
     */
    private String bucketName;

}
