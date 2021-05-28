package com.dlink.db.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MybatisPlusFillProperties
 *
 * @author wenmo
 * @since 2021/5/25
 **/
@Setter
@Getter
@ConfigurationProperties(prefix = "dlink.mybatis-plus.fill")
public class MybatisPlusFillProperties {

    private Boolean enabled = true;

    private Boolean enableInsertFill = true;

    private Boolean enableUpdateFill = true;

    private String createTimeField = "createTime";

    private String updateTimeField = "updateTime";
}
