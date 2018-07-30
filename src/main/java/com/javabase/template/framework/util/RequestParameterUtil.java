package com.javabase.template.framework.util;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javabase.template.framework.exception.TemplateRuntimeException;

/**
 * Request Parameter Util
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class RequestParameterUtil {
    protected static Logger logger = LoggerFactory.getLogger(RequestParameterUtil.class);

    private RequestParameterUtil() { /* do nothing */ }

    /** 파라미터 획득(없으면 예외발생) */
    public static String getRequiredParam(ServletRequest request, String name) throws RuntimeException {
        String value = request.getParameter(name);
        if(StringUtils.isEmpty(value)) {
            throw new TemplateRuntimeException(name + " 파라미터는 필수입니다.");
        }
        return value;
    }

    /** 파라미터 int 획득(없거나, 숫자가 아니면 예외발행) */
    public static int getRequiredParamAsInt(ServletRequest request, String name) throws RuntimeException {
        String value = getRequiredParam(request, name);
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ex) {
            throw new TemplateRuntimeException(name + " 파라미터는 정수형이여야 합니다.");
        }
    }

    /** 파라미터 Integer 획득(없거나, 숫자가 아니면 예외발행) */
    public static Integer getParamAsInteger(ServletRequest request, String name) throws RuntimeException {
        String value = request.getParameter(name);
        if(StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ex) {
            throw new TemplateRuntimeException(name + " 파라미터는 정수형이여야 합니다.");
        }
    }

    /** 파라미터 boolean 획득(없으면 예외발생, 'y', 'true', '1'이면 true, 'n', 'false', '0'이면 false. 이외에는 오류) */
    public static boolean getRequiredParamAsBool(ServletRequest request, String name) throws RuntimeException {
        String value = getRequiredParam(request, name);
        Boolean booleanValue = ConvertUtil.toBoolean(value);
        if(booleanValue == null) {
            throw new TemplateRuntimeException(name + " 파라미터는 필수입니다.");
        }
        return booleanValue;
    }

    /** 파라미터 boolean 획득(없으면 예외발생, 'y', 'true', '1'이면 true, 'n', 'false', '0'이면 false. 이외에는 오류) */
    public static Boolean getParamAsBoolean(ServletRequest request, String name) throws RuntimeException {
        String value = request.getParameter(name);
        return ConvertUtil.toBoolean(value);
    }
}
