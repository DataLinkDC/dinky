package com.dlink.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * User
 *
 * @author wenmo
 * @since 2021/5/28 15:57
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable{

    private static final long serialVersionUID = -1077801296270024204L;

    private String username;

    private String password;
}
