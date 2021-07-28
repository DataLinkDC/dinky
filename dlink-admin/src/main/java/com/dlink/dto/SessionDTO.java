package com.dlink.dto;

import com.dlink.session.SessionConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * SessionDTO
 *
 * @author wenmo
 * @since 2021/7/6 22:10
 */
@Getter
@Setter
public class SessionDTO {
    private String session;
    private String type;
    private boolean useRemote;
    private Integer clusterId;

}
