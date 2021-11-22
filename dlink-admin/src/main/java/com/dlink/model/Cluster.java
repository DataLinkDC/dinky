package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Cluster
 *
 * @author wenmo
 * @since 2021/5/28 13:53
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_cluster")
public class Cluster extends SuperEntity {

    private static final long serialVersionUID = 3104721227014487321L;

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String type;

    private String hosts;

    private String jobManagerHost;

    private String version;

    private Integer status;

    private String note;

    private boolean autoRegisters;

    private Integer clusterConfigurationId;

    private Integer taskId;

    public static Cluster autoRegistersCluster(String hosts,String name,String alias,String type,Integer clusterConfigurationId,Integer taskId){
        Cluster cluster =  new Cluster();
        cluster.setName(name);
        cluster.setAlias(alias);
        cluster.setHosts(hosts);
        cluster.setType(type);
        cluster.setClusterConfigurationId(clusterConfigurationId);
        cluster.setTaskId(taskId);
        cluster.setAutoRegisters(true);
        cluster.setEnabled(true);
        return cluster;
    }
}
