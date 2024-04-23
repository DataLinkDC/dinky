package org.dinky.configure.propertie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The pac4j configuration and callback URL.
 *
 * @author yangzehan
 *
 */
@ConfigurationProperties(prefix = "pac4j", ignoreUnknownFields = false)
@Getter
@Setter
public class Pac4jConfigurationProperties {
    private Map<String, String> properties = new LinkedHashMap<>();
    private Map<String, String> callback = new LinkedHashMap<>();
    private Map<String, String> centralLogout = new LinkedHashMap<>();
    private Map<String, String> logout = new LinkedHashMap<>();
    private String callbackUrl;

}




