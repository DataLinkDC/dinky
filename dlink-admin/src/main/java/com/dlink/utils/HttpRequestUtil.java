package com.dlink.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * http util
 */
@Slf4j
public class HttpRequestUtil {
    /**
     * @param request
     * @return
     */
    public static String getRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (ServletInputStream servletInputStream = request.getInputStream()) {
            String line = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(servletInputStream));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("get request inputStream for body error,message={}", e.getMessage());
            e.printStackTrace();
        }
        return sb.toString();
    }
}
