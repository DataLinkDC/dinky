package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.annotation.Save;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SysConfig
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_sys_config")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 3769276772487490408L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "配置名不能为空", groups = {Save.class})
    private String name;

    private String value;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    protected Serializable pkVal() {
        return this.id;
    }


}
