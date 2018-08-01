package com.javabase.template.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.javabase.template.framework.util.WebUtil;

/**
 * Interceptor
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class TemplateInterceptor extends HandlerInterceptorAdapter {
    protected final Logger logger = LoggerFactory.getLogger(TemplateInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String ipAddress = WebUtil.getClientIP(request);

            StringBuilder sb = new StringBuilder();
            sb.append("\r\n############################################################");
            sb.append("\r\n### SPRING TEMPLATE INTERCEPTOR");
            sb.append("\r\n### Client IP: " + ipAddress);
            logger.debug(sb.toString());
            return true;
        } catch(Exception ex) {
            logger.debug("\r\n### Filter Result: preHandler Exception\r\n***  - exception info: {}" + ex.getMessage(), ex);
            throw ex;
        }
    }
}
