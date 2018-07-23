package com.javabase.template.framework.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Web 관련 헬퍼 객체
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class WebUtil {

    private WebUtil() { /*do nothing*/ }

    /** POST Form 요청인가 확인 */
    public static boolean isFormPost(HttpServletRequest request) {
        String contentType = request.getContentType();
        String method = request.getMethod();
        return contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) && HttpMethod.POST.matches(method);
    }

    /**
     * 요청헤더를 Map으로 획득
     */
    public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValues = request.getHeader(headerName);
            headers.put(headerName, headerValues);
        }
        return headers;
    }

    /**
     * 응답헤더를 Map으로 획득
     */
    public static Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for(String headerName : headerNames) {
            String headerValues = response.getHeader(headerName);
            headers.put(headerName, headerValues);
        }
        return headers;
    }
}
