package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
import com.dlink.db.annotation.Save;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Savepoints
 *
 * @author wenmo
 * @since 2021/11/21 16:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_savepoints")
public class Savepoints implements Serializable {

    private static final long serialVersionUID = 115345627846554078L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "作业ID不能为空", groups = {Save.class})
    private Integer taskId;

    private String name;

    private String type;

    private String path;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    protected Serializable pkVal() {
        return this.id;
    }
}
