package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import com.dlink.metadata.driver.DriverConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

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

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String groupName;

    private String type;

    private String url;

    private String username;

    private String password;

    private String note;

    private String dbVersion;

    private boolean status;

    private LocalDateTime healthTime;

    private LocalDateTime heartbeatTime;

    public DriverConfig getDriverConfig(){
        return new DriverConfig(type,url,username,password);
    }
}
