package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.assertion.Asserts;
import com.dlink.db.model.SuperEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterConfig
 *
 * @author wenmo
 * @since 2021/11/6
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_cluster_configuration")
public class ClusterConfiguration extends SuperEntity {

    private static final long serialVersionUID = 5830130188542066241L;

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String type;

    private String configJson;

    private boolean isAvailable;

    private String note;

    @TableField(exist = false)
    private Map<String,Object> config = new HashMap<>();


    public Map<String,Object> parseConfig(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(Asserts.isNotNullString(configJson)) {
                config = objectMapper.readValue(configJson, HashMap.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return config;
    }
}
