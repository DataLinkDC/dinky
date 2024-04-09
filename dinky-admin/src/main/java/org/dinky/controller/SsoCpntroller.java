package org.dinky.controller;

import lombok.NoArgsConstructor;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.AuthException;
import org.dinky.data.result.Result;
import org.dinky.service.UserService;
import org.dinky.sso.web.LogoutController;
import org.pac4j.core.config.Config;


import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * @author 杨泽翰
 */
@RestController
@NoArgsConstructor
@ConfigurationProperties(prefix = "pac4j")
public class SsoCpntroller {



    @Value("${sso.centralLogout.defaultUrl:#{null}}")
    private String defaultUrl;

    @Value("${sso.centralLogout.logoutUrlPattern:#{null}}")
    private String logoutUrlPattern;
    @Value("${pac4j.properties.principalNameAttribute:#{null}}")
    private String principalNameAttribute;
    @Autowired
    private Config config;
    @Autowired
    private JEEContext webContext;
    @Autowired
    private ProfileManager profileManager;
    private LogoutController logoutController;

    @Autowired
    private UserService userService;

    @PostConstruct
    protected void afterPropertiesSet() {
        logoutController = new LogoutController();
        logoutController.setDefaultUrl(defaultUrl);
        logoutController.setLogoutUrlPattern(logoutUrlPattern);
        logoutController.setLocalLogout(true);
        logoutController.setCentralLogout(true);
        logoutController.setConfig(config);
        logoutController.setDestroySession(true);
    }

    @GetMapping ("/sso/token")
    public Result<UserDTO> token() throws AuthException {

        System.out.println(config);
        List<CommonProfile> all = profileManager.getAll(true);
        String username = all.get(0).getAttribute(principalNameAttribute).toString();
        if (username == null){
            throw new AuthException(Status.NOT_MATCHED_PRINCIPAL_NAME_ATTRIBUTE);
        }
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setSsoLogin(true);
        return  userService.loginUser(loginDTO);
    }
    @GetMapping ("/sso/logout")
    public void logout() {
        logoutController.logout(webContext.getNativeRequest(), webContext.getNativeResponse());
    }

}
