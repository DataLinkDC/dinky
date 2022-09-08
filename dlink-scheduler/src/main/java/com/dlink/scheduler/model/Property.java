package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.DataType;
import com.dlink.scheduler.enums.Direct;

import java.io.Serializable;

import lombok.Data;

@Data
public class Property implements Serializable {

    private static final long serialVersionUID = -4045513703397452451L;
    /**
     * key
     */
    private String prop;

    /**
     * input/output
     */
    private Direct direct;

    /**
     * data type
     */
    private DataType type;

    /**
     * value
     */
    private String value;

}
