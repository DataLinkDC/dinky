package org.dinky.ltpa.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dinky.ltpa.config.LtpaTokenProperties;
import org.dinky.ltpa.config.LtpaTokenSetting;
import org.dinky.ltpa.token.LtpaToken;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Author: lwjhn
 * Date: 2023/6/30 17:49
 * Description:
 */
@Component
@Slf4j
public class LtpaTokenHandler {
    @Resource
    LtpaTokenProperties ltpaTokenProperties;

    public boolean isLtpaTokenCall(HttpServletRequest request) {
        return iterator(request, (token, entry) -> true, false);
    }

    public LtpaToken validateLtpaToken(HttpServletRequest request) {
        return iterator(request, (token, entry) -> {
            LtpaToken ltpaToken = new LtpaToken(token);
            ltpaToken.validate(entry.getValue());
            return ltpaToken;
        }, null);
    }


/*    public LtpaAuthenticationToken buildToken(HttpServletRequest request) {
        return iterator(request, (token, entry) -> new LtpaAuthenticationToken(new LtpaToken(token), entry));
    }*/

/*    public static Long getUser(LtpaAuthenticationToken authenticationToken) {
        LtpaToken ltpaToken = authenticationToken.getLtpaToken();
        Map.Entry<String, LtpaTokenSetting> credentials = authenticationToken.getSetting();
        LtpaTokenSetting setting = credentials.getValue();
        try {
            ltpaToken.validate(setting);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + " ( " + credentials.getKey() + " )");
        }
        return Long.parseLong(setting.getTargetRegExp() == null ? ltpaToken.getCanonicalUser()
                : setting.getTargetRegExp().matcher(ltpaToken.getCanonicalUser()).replaceAll(setting.getTargetRegVal()));
    }*/

/*    public Long getUser(HttpServletRequest request) {
        LtpaAuthenticationToken ltpaAuthenticationToken = buildToken(request);
        return ltpaAuthenticationToken == null ? null : getUser(ltpaAuthenticationToken);
    }*/

    public LtpaToken generate(long user) {
        LtpaTokenSetting setting;
        for (Map.Entry<String, LtpaTokenSetting> entry : ltpaTokenProperties.entrySet()) {
            if ((setting = entry.getValue()) != null) {
                return LtpaToken.generate(String.valueOf(user), setting);
            }
        }
        return null;
    }

    public final static String LTPA_COOKIE_HEADER = "ltpa-cookie-";
    public final static String ACCESS_HEADER = "Access-Control-Expose-Headers";

/*    public String setCookie(long user) {
        LtpaAuthenticationToken authenticationToken = generate(user);
        if (authenticationToken == null || authenticationToken.getLtpaToken() == null) {
            return null;
        }
        String domain = authenticationToken.getSetting().getValue().getDomain();
        LtpaToken token = authenticationToken.getLtpaToken();
        HttpServletResponse httpServletResponse = ServletUtils.response();
        httpServletResponse.addHeader(ACCESS_HEADER, LTPA_COOKIE_HEADER + authenticationToken.getSetting().getKey());
        httpServletResponse.setHeader(LTPA_COOKIE_HEADER + authenticationToken.getSetting().getKey(),
                domain = authenticationToken.getSetting().getKey() + "=" + token.getLtpaToken() +
                        "; Expires=" + token.getExpiresDate() + "; Path=/" +
                        (StringUtils.isBlank(domain) ? "" : ("; Domain=" + domain)));
        return domain;
    }*/

    /*public void removeCookie() {
        HttpServletResponse httpServletResponse = ServletUtils.response();
        List<String> clearList;
        String value;
        for (Map.Entry<String, LtpaTokenSetting> entry : ltpaTokenProperties.entrySet()) {
            if (entry.getValue() != null) {
                httpServletResponse.addHeader(ACCESS_HEADER, LTPA_COOKIE_HEADER + entry.getKey());
                httpServletResponse.setHeader(LTPA_COOKIE_HEADER + entry.getKey(),
                        entry.getKey() + (
                                value = "=" + "; Expires=" + new Date() + "; Path=/" +
                                        (StringUtils.isBlank(entry.getValue().getDomain()) ? "" : ("; Domain=" + entry.getValue().getDomain()))
                        ));
                if ((clearList = entry.getValue().getClearCookies()) != null) {
                    for (String key : clearList) {
                        httpServletResponse.addHeader(ACCESS_HEADER, LTPA_COOKIE_HEADER + key);
                        httpServletResponse.setHeader(LTPA_COOKIE_HEADER + key, key + value);
                    }
                }

            }
        }
    }*/

    public <T> T iterator(HttpServletRequest request, BiFunction<String, Map.Entry<String, LtpaTokenSetting>, T> caller) {
        return iterator(request, caller, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T iterator(HttpServletRequest request, BiFunction<String, Map.Entry<String, LtpaTokenSetting>, T> caller, T defaultValue) {
        if (request == null || ltpaTokenProperties == null) {
            return defaultValue;
        }
        Map<String, Cookie> cookies = request.getCookies() == null ? Collections.EMPTY_MAP
                : Arrays.stream(request.getCookies()).collect(Collectors.toMap(Cookie::getName, cookie -> cookie));
        for (Map.Entry<String, LtpaTokenSetting> entry : ltpaTokenProperties.entrySet()) {
            Cookie cookie = cookies.get(entry.getKey());
            String token = cookie == null ? null : cookie.getValue();
            if (entry.getValue() != null && (StringUtils.isNotBlank(token) || StringUtils.isNotBlank(token = request.getHeader(entry.getKey())))) {
                return caller.apply(token, entry);
            }
        }
        return defaultValue;
    }
}
