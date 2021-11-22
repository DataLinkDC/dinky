package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Jar
 *
 * @author wenmo
 * @since 2021/11/13
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_jar")
public class Jar extends SuperEntity {

    private static final long serialVersionUID = 3769276772487490408L;

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String type;

    private String path;

    private String mainClass;

    private String paras;

    private String note;
}
