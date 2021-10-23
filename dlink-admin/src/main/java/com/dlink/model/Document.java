package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Document
 *
 * @author wenmo
 * @since 2021/6/3 14:27
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_flink_document")
public class Document extends SuperEntity {

    private static final long serialVersionUID = -6340080980759236641L;
    private String category;
    private String type;
    private String subtype;
    private String description;
    private String version;
    private String fillValue;
    private String likeNum;
}
