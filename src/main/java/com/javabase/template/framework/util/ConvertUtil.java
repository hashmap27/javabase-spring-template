package com.javabase.template.framework.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javabase.template.framework.exception.TemplateRuntimeException;

/**
 * 각종 변환 Util
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class ConvertUtil {
    protected Logger logger = LoggerFactory.getLogger(ConvertUtil.class);

    private ConvertUtil() { /* do nothing */ }

    /**
     * boolean 획득 ('y', 'true', '1'이면 true / 'n', 'false', 0'이면 false / 이외에는 오류)
     */
    public static boolean toBool(String value) {
        Boolean boolValue = toBoolean(value);
        if(boolValue == null) {
            throw new TemplateRuntimeException("Cannot parse bool value: " + value);
        }
        return boolValue;
    }

    /**
     * boolean 획득 ('y', 'true', '1'이면 true / 'n', 'false', 0'이면 false / 이외에는 null)
     */
    public static Boolean toBoolean(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }
        String upperValue = value.toUpperCase();
        if("Y".equals(upperValue) || "TRUE".equals(upperValue) || "1".equals(upperValue)) {
            return true;
        }
        if("N".equals(upperValue) || "FALSE".equals(upperValue) || "0".equals(upperValue)) {
            return false;
        }
        return null;
    }

    /**
     * boolean 획득 ('y', 'true', '1'이면 true / 'n', 'false', 0'이면 false / 이외에는 기본값)
     */
    public static boolean toBool(String value, boolean defaultValue) {
        Boolean boolValue = toBoolean(value);
        if(boolValue == null) {
            return defaultValue;
        }
        return boolValue;
    }

    /**
     * boolean 획득 ('y', 'true', '1'이면 true / 'n', 'false', 0'이면 false / 이외에는 기본값)
     */
    public static Boolean toBoolean(String value, Boolean defaultValue) {
        Boolean boolValue = toBoolean(value);
        if(boolValue == null) {
            return defaultValue;
        }
        return boolValue;
    }

    /**
     * Integer로 변환 실패 시 Null로 반환
     */
    public static Integer toInteger(String value) {
        return toInteger(value, null);
    }

    /**
     * Integer로 변환 실패 시 기본값 반환
     */
    public static Integer toInteger(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * int로 변환 실패 시 0으로 반환
     */
    public static int toInt(String value) {
        return toInt(value, 0);
    }

    /**
     * int로 변환 실패 시 기본값으로 반환
     */
    public static int toInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * Long으로 변환 실패 시 Null로 반환
     */
    public static Long toLong(String value) {
        return toLong(value, null);
    }

    /**
     * Long으로 변환 실패 시 기본값으로 반환
     */
    public static Long toLong(String value, Long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch(NumberFormatException ex) {
            return defaultValue;
        }
    }

    /** Map<String, ?>를 List<NameValuePair>로 변환  */
    public static List<NameValuePair> convertMapToNameValuePairList(Map<String, ?> parameters) {
        List<NameValuePair> paramNameValuePairList = new ArrayList<>();
        for(Map.Entry<String, ?> item : parameters.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            String valueString = (value == null) ? "" : value.toString();
            paramNameValuePairList.add(new BasicNameValuePair(key, valueString));
        }
        return paramNameValuePairList;
    }

    /** 숫자에 천단위 콤마 포매팅 */
    public static String formatNumberComma(long number) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    }
    /** 숫자에 천단위 콤마 포매팅 */
    public static String formatNumberComma(Double number) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    }
    /** 숫자에 천단위 콤마 포매팅 */
    public static String formatNumberComma(Object number) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    }

}
