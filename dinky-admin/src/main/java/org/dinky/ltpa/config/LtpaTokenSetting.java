package org.dinky.ltpa.config;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: lwjhn
 * Date: 2023/6/30 14:23
 * Description:
 */
public class LtpaTokenSetting extends LtpaSetting {
    private Pattern originRegExp;

    private String originRegVal;

    private Pattern targetRegExp;

    private String targetRegVal;
    private String domain;

    private List<String> clearCookies;

    public Pattern getOriginRegExp() {
        return originRegExp;
    }

    public void setOriginRegExp(Pattern originRegExp) {
        this.originRegExp = originRegExp;
    }

    public String getOriginRegVal() {
        return originRegVal;
    }

    public void setOriginRegVal(String originRegVal) {
        this.originRegVal = originRegVal;
    }

    public Pattern getTargetRegExp() {
        return targetRegExp;
    }

    public void setTargetRegExp(Pattern targetRegExp) {
        this.targetRegExp = targetRegExp;
    }

    public String getTargetRegVal() {
        return targetRegVal;
    }

    public void setTargetRegVal(String targetRegVal) {
        this.targetRegVal = targetRegVal;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getClearCookies() {
        return clearCookies;
    }

    public void setClearCookies(List<String> clearCookies) {
        this.clearCookies = clearCookies;
    }
}
