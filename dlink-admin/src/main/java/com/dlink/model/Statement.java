package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Statement
 *
 * @author wenmo
 * @since 2021/5/28 13:39
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_task_statement")
public class Statement implements Serializable {

    private static final long serialVersionUID = 1646348574144815792L;

    private Integer id;

    private String statement;
}
