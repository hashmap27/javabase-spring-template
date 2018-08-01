package com.javabase.template.framework.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client에서 올라온 검색조건 맵 (prefix: search)
 *
 * put("name", value) => searchName으로 저장
 * get("name"), get("searchName")으로 조회 가능
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class SearchMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -550579983856255080L;
    private static final Logger logger = LoggerFactory.getLogger(SearchMap.class);

    @Override
    public Object put(String key, Object valueParam) {
        Object value = valueParam;
        if(key == null || value == null) {
            return super.put(key, value);
        }
        if(StringUtils.equals(value.toString(), "true")) {
            value = true;
        }
        if(StringUtils.equals(value.toString(),  "false")) {
            value = false;
        }
        String keyToApply = StringUtils.removeStart(key, "search");
        keyToApply = "search" + capitalizeFully(keyToApply);
        return super.put(keyToApply, value);
    }

    @Override
    public Object get(Object keyObj) {
        if(keyObj == null) {
            return super.get(keyObj);
        }
        String key = (String) keyObj;
        if(!containsKey(key)) {
            key = "search" + capitalizeFully(key);
        }
        return super.get(key);
    }

    /** put 메소드와 동일, fluent api 형태 */
    public SearchMap add(String key, Object value) {
        this.put(key, value);
        return this;
    }

    /** 생성자 - 주어진 맵에서 search로 시작하는 파라미터만 담는다. decodeURIComponent 적용 */
    public SearchMap(Map<? extends String, ? extends Object> map) {
        for(Map.Entry<? extends String, ? extends Object> entry : map.entrySet()) {
            if(entry.getKey().startsWith("search")) {
                if(entry.getValue() instanceof String[]) {
                    //String array value
                    String[] values = (String[]) entry.getValue();
                    if(values.length == 1) {
                        this.put(entry.getKey(), decodeURIComponent(values[0]));
                        continue;
                    } else if(values.length > 1) {
                        for (String value : values) {
                            value = decodeURIComponent(value);
                        }
                        this.put(entry.getKey(), Arrays.asList(values));
                    }
                } else if(entry.getValue() instanceof String) {
                    //String value
                    this.put(entry.getKey(), decodeURIComponent((String)entry.getValue()));
                } else {
                    //Other
                    this.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /** Client에서 encoding하여 올린 문자열 decoding 처리 */
    private String decodeURIComponent(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch(UnsupportedEncodingException ex) {
            logger.trace("{} - {}", ex.getClass().getName(), ex.getMessage(), ex);
            return value;
        }
    }

    /** WordUtils.capitalizeFully의 deprecated로 해당 code 참고하여 private method로 변환 처리. */
    private String capitalizeFully(String str) {
        String target = str.toLowerCase();
        int targetLen = target.length();
        int[] newCodePoints = new int[targetLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for(int index = 0; index < targetLen;) {
            final int codePoint = target.codePointAt(index);

            if(Character.isWhitespace(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if(capitalizeNext) {
                int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /** 기본 생성자 */
    public SearchMap() { /* do nothing */ }

    /** 생성자 - Request의 ParameterMap에서 search로 시작하는 파라미터로 생성 */
    public SearchMap(ServletRequest request) {
        this(request.getParameterMap());
    }

    /** Factory - Request의 Map으로부터 생성(POST form이나 Get queryString의 파라미터맵에서 생성) */
    public static SearchMap buildFrom(Map<? extends String, ? extends Object> map) {
        return new SearchMap(map);
    }

    /** Factory - Request의 ParameterMap에서 search로 시작하는 파라미터로 생성 */
    public static SearchMap buildFrom(ServletRequest request) {
        return new SearchMap(request);
    }

    /** Factory */
    public static SearchMap create() {
        return new SearchMap();
    }

}
