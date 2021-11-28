package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * LoginUTO
 *
 * @author wenmo
 * @since 2021/11/28 17:02
 */
@Getter
@Setter
public class LoginUTO {
    private String username;
    private String password;
    private boolean autoLogin;
}
