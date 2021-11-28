package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
import com.dlink.assertion.Asserts;
import com.dlink.db.annotation.Save;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User
 *
 * @author wenmo
 * @since 2021/5/28 15:57
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_user")
public class User implements Serializable{

    private static final long serialVersionUID = -1077801296270024204L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "用户名不能为空", groups = {Save.class})
    private String username;

    private String password;

    private String nickname;

    private String worknum;

    private byte[] avatar;

    private String mobile;

    private boolean enabled;

    private boolean isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private boolean isAdmin;
}
