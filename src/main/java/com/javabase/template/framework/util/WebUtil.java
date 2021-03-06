package com.javabase.template.framework.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * Web 관련 헬퍼 객체
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class WebUtil {

    private WebUtil() { /*do nothing*/ }

    private static final String LOCAL_IP = "127.0.0.1";

    /**  실행중인 WAS의 Container명 획득 */
    public static String getWasContainerName() {
        //Weblogic WAS Container Name
        String name = System.getProperty("weblogic.Name");
        if(!StringUtils.isEmpty(name)) {
            return name;
        }
        //NOTE: 다른 WAS 컨테이너에 대한 대응 필요시 여기 추가.
        return "";
    }

    /** WEB Proxy를 통한 접근인가? */
    public static boolean isProxyRequest(HttpServletRequest request) {
        String clientIP = request.getHeader("client-ip");   //L7을 통한 경우 L7-WEB-WAS
        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        return !StringUtils.isEmpty(clientIP) || !StringUtils.isEmpty(proxyClientIp) || !StringUtils.isEmpty(xForwardedFor) ? true : false;
    }

    /** LocalHost, LocalIP 를 통한 접근? */
    public static boolean isLocalhostRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        //remoteAddr == localAddr : LocalIP를 통한 접근
        //0:0:0:0:0:0:0:1 == remoteAddr : LocalHost를 통한 접근
        return remoteAddr.equals(localAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr) ? true : false;
    }

    /**
     * x-forwarded-for 헤더 또는 RemoteAddr 획득
     * @param request 요청 객체
     * @return 접속 Client IP
     */
    public static String getClientIP(ServletRequest request) {
        //원격 접속 IP 획득
        String ipAddress = null;
        if(request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            ipAddress = servletRequest.getHeader("x-forwarded-for");    //WEB을 거쳐서 WAS에 온 경우 Origin IP
            if(StringUtils.isEmpty(ipAddress)) {
                ipAddress = servletRequest.getHeader("client-ip");
            }
            if(StringUtils.isEmpty(ipAddress)) {
                ipAddress = servletRequest.getHeader("Proxy-Client-IP");
            }
            if(StringUtils.isEmpty(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
        }

        if(ipAddress != null && ipAddress.indexOf(',') >= 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
        }
        if("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = LOCAL_IP;
        }

        return ipAddress;
    }

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
