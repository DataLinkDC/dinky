package org.dinky.data.model.ext;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liurulin
 * @version 6.1.0
 * @description 描述
 * @date 2024/1/4 14:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "scheduleConfig", description = "调度配置")
public class ScheduleConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "schedulingType",
            notes = "调度类型")
    private String schedulingType;

    @ApiModelProperty(
            value = "effectiveDateEnd",
            notes = "生效开始时间")
    private Date effectiveDateEnd;

    @ApiModelProperty(
            value = "effectiveDateStart",
            notes = "生效结束时间")
    private Date effectiveDateStart;

    @ApiModelProperty(
            value = "periodType",
            notes = "调度周期")
    private String periodType;

    @ApiModelProperty(
            value = "periodType",
            notes = "指定日")
    private String[] days;

    @ApiModelProperty(
            value = "periodType",
            notes = "调度周")
    private String[] weeks;

    @ApiModelProperty(
            value = "periodTime",
            notes = "时间/指定时间")
    private Date periodTime;


}
