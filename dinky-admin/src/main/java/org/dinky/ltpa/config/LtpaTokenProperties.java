package org.dinky.ltpa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

/**
 * Author: lwjhn
 * Date: 2023/6/30 11:21
 * Description:
 */
@ConfigurationProperties(prefix = "ltpa", ignoreInvalidFields = true)
public class LtpaTokenProperties extends HashMap<String, LtpaTokenSetting> {

}
