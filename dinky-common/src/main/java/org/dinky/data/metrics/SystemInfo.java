package org.dinky.data.metrics;

import java.util.Map;
import java.util.Properties;
import lombok.Data;

@Data
public class SystemInfo {
    private String osName;
    private String osArch;
    private String osVersion;
    private String jvmVersion;
    private String jvmVendor;
    private String jvmName;
    private String computerName;
    private String computerUser;

    public static SystemInfo of() {
        SystemInfo system = new SystemInfo();
        Properties props = System.getProperties();
        system.setOsName(props.getProperty("os.name"));
        system.setOsArch(props.getProperty("os.arch"));
        system.setOsVersion(props.getProperty("os.version"));
        system.setJvmVersion(props.getProperty("java.vm.version"));
        system.setJvmVendor(props.getProperty("java.vm.vendor"));
        system.setJvmName(props.getProperty("java.vm.name"));
        Map<String, String> map = System.getenv();
        system.setComputerName(map.get("NAME"));
        system.setComputerUser(map.get("USER"));
        return system;
    }
}
