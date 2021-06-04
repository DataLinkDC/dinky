package com.dlink.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AntDesignController
 *
 * @author wenmo
 * @since 2021/6/4 9:00
 **/
@Controller
public class AntDesignController implements ErrorController {
    @Override
    public String getErrorPath(){
        return "/error";
    }
    @RequestMapping(value = "/error")
    public String getIndex(){
        return "index"; //返回index页面
    }

    /*@RequestMapping("/api/**")
    public ApiResult api(HttpServletRequest request, HttpServletResponse response){
        return apiProxy.proxy(request, response);
    }

    @RequestMapping(value="/**", method=HTTPMethod.GET)
    public String index(){
        return "index";
    }*/
}