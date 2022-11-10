package com.dlink.internationalization;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @Author MRyan
 * @Date 2020/2/5 15:26
 * @Version 1.0
 */
public class LocaleResolverConfiguration implements LocaleResolver {

    List<Locale> LOCALES = Arrays.asList(
            new Locale("en-US"),
            new Locale("zh-CN"));
    //解析请求
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        //获取请求中的参数链接
        String requestHeader = request.getHeader("Accept-Language");
        if (requestHeader == null || requestHeader.isEmpty()){
            return Locale.SIMPLIFIED_CHINESE;
        }else {
            return Locale.lookup(Locale.LanguageRange.parse(requestHeader), LOCALES);
        }

//        String language = request.getParameter("umi_locale");//前端传递的参数用来判断中文还是英文

//        Locale locale = Locale.getDefault();//获取默认Locale
//
//        if (Asserts.isNotNullString(language)) {//判断传递参数是否为空
//            //zh_CN
//            String[] split = language.split("_");//前后分割 分开获取国家和地区
//            //国家，地区
//            locale = new Locale(split[0], split[1]);
//        }
//        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
