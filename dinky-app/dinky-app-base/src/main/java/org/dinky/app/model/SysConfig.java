package org.dinky.app.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String value;
}
