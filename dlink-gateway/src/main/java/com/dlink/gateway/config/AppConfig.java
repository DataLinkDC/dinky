package com.dlink.gateway.config;

import com.dlink.assertion.Asserts;
import lombok.Getter;
import lombok.Setter;

/**
 * AppConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:55
 */
@Setter
@Getter
public class AppConfig {
    private String userJarPath;
    private String[] userJarParas;
    private String userJarMainAppClass;

    public AppConfig() {
    }

    public AppConfig(String userJarPath, String[] userJarParas, String userJarMainAppClass) {
        this.userJarPath = userJarPath;
        this.userJarParas = userJarParas;
        this.userJarMainAppClass = userJarMainAppClass;
    }

    public static AppConfig build(String userJarPath, String userJarParasStr, String userJarMainAppClass){
        if(Asserts.isNotNullString(userJarParasStr)){
            return new AppConfig(userJarPath,userJarParasStr.split(" "),userJarMainAppClass);
        }else{
            return new AppConfig(userJarPath,new String[]{},userJarMainAppClass);

        }
    }
}
