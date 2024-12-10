package org.dinky.data.model;

import alluxio.shaded.client.com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dinky.serializer.LocalDateTimeDeserializer;
import org.dinky.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_approval")
@ApiModel(value = "Approval", description = "Approval instance")
public class Approval extends Model<Approval> {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", dataType = "Integer", notes = "Unique identifier for the approval")
    private Integer id;

    @ApiModelProperty(value = "Task Id", dataType = "Integer", notes = "Task identifier for the task approval linked")
    private Integer taskId;

    @ApiModelProperty(value = "Previous Task Version", dataType = "Integer", notes = "Previous online version before this task is submitted")
    private Integer previousTaskVersion;

    @ApiModelProperty(value = "Current Task Version", dataType = "Integer", notes = "Task version required for publish")
    private Integer currentTaskVersion;

    @ApiModelProperty(value = "Approval status", dataType = "String", notes = "Approval status")
    private String status;

    @ApiModelProperty(value = "Submitter", dataType = "Integer", notes = "Submitter user id")
    private Integer submitter;

    @ApiModelProperty(value = "Submitter Comment", dataType = "String", notes = "Submitter comment")
    private String submitterComment;

    @ApiModelProperty(value = "Reviewer", dataType = "Integer", notes = "Reviewer user id")
    private Integer reviewer;

    @ApiModelProperty(value = "Reviewer Comment", dataType = "String", notes = "Reviewer comment")
    private String reviewerComment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "Create Time", dataType = "Date", notes = "Timestamp when the approval was created")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "Update Time", dataType = "Date", notes = "Timestamp when the approval was updated")
    private LocalDateTime updateTime;
}
