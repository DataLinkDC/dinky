package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Catalogue
 *
 * @author wenmo
 * @since 2021/5/28 13:51
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_catalogue")
public class Catalogue extends SuperEntity {

    private static final long serialVersionUID = 4659379420249868394L;

    private Integer taskId;

    private String type;

    private Integer parentId;

    private Boolean isLeaf;
}
