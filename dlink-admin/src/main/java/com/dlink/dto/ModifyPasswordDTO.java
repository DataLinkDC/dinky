package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ModifyPasswordDTO
 *
 * @author wenmo
 * @since 2022/2/22 23:27
 */
@Getter
@Setter
public class ModifyPasswordDTO {
    private String username;
    private String password;
    private String newPassword;
}
