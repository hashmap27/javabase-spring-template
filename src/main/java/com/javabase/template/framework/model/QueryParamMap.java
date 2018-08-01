package com.javabase.template.framework.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 쿼리파라미터 Map<String, Object>를 쉽게 빌드하기 위한 헬퍼
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class QueryParamMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -4543686516917311486L;

    private static final HashMap<String, Object> EMPTY_MAP = new HashMap<>();

    /** 팩토리 메서드 */
    public static QueryParamMap create() {
        return new QueryParamMap();
    }

    /** 생성자 */
    public QueryParamMap() {
        super();
        //NOTE: 향후 Spring Security등이 추가되어 로그인정보를 me로 Map에 추가 가능.
    }

    /** 생성자 */
    public QueryParamMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        //NOTE: 향후 Spring Security등이 추가되어 로그인정보를 me로 Map에 추가 가능.
    }

    /** 생성자 */
    public QueryParamMap(int initialCapacity) {
        super(initialCapacity);
        //NOTE: 향후 Spring Security등이 추가되어 로그인정보를 me로 Map에 추가 가능.
    }

    /** 생성자 */
    public QueryParamMap(Map<? extends String, ? extends Object> m) {
        super(m);
        //NOTE: 향후 Spring Security등이 추가되어 로그인정보를 me로 Map에 추가 가능.
    }

    public QueryParamMap add(String key, Object value) {
        this.put(key, value);
        return this;
    }

    /**
     * 키로 값 획득
     *  - 값이 null이여도 mybatis에서 하위값 탐색에 오류가 발생하지 않게 empty_map을 반환 처리
     */
    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if(value == null) {
            return EMPTY_MAP;
        }
        return value;
    }

    //NOTE: 향후 Spring Security등이 추가되어 로그인정보를 me로 Map에 추가 가능.
    //값이 null이여도 mybatis에서 하위값 탐색에 오류가 발생하지 않게 empty_map을 반환 처리


}
