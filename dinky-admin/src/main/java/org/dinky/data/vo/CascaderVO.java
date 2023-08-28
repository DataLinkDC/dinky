package org.dinky.data.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class CascaderVO implements Serializable {
    private String value;
    private String label;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CascaderVO> children;
}
