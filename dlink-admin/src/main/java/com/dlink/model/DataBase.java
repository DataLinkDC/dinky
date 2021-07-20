package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DataBase
 *
 * @author wenmo
 * @since 2021/7/20 20:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_database")
public class DataBase extends SuperEntity {

    private static final long serialVersionUID = -5002272138861566408L;

    private String alias;

    private String groupName;

    private String type;

    private String ip;

    private Integer port;

    private String url;

    private String username;

    private String password;

    private String note;

    private String dbVersion;
}
