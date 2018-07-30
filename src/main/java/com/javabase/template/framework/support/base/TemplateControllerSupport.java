package com.javabase.template.framework.support.base;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.javabase.template.framework.util.ReflectionUtil;

/**
 * Javabase Spring Template Controller Support
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public abstract class TemplateControllerSupport extends TemplateComponentSupport {

    /** 현재 메소드의 Qualified명을 획득 (패키지.클래스.메소드명) */
    protected String getCurrentMethodAualifiedName() {
        return this.getClass().getSimpleName() + "." + ReflectionUtil.getCurrentMethodName();
    }

    /**
     * Request에서 req파라미터 문자열을 취득(\r\n trimcjfl)
     * @param request 요청 객체
     * @return req파라미터 문자열
     */
    protected String getReqParam(HttpServletRequest request) {
        String req = request.getParameter("req");
        req = StringUtils.trimTrailingCharacter(req, '\r');
        req = StringUtils.trimTrailingCharacter(req, '\n');
        return req;
    }

}
