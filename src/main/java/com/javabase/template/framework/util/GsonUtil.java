package com.javabase.template.framework.util;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.javabase.template.framework.gson.joda.GsonConverters;

/**
 * Google Gson 헬퍼
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class GsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);

    private GsonUtil() { /* do nothing */ }

    /** Singleton Instance */
    private static Gson gsonInstance = null;

    public static Gson getGsonInstance() {
        if(gsonInstance == null) {
            gsonInstance = buildDefaultGson();
        }
        return gsonInstance;
    }

    /** Gson 객체 기본설정으로 생성 */
    public static Gson buildDefaultGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        GsonConverters.registerAllConverters(gsonBuilder);
        return gsonBuilder.serializeNulls().create();
    }

    /** 주어진 객체를 Json으로 직렬화 */
    public static String toJson(Object object) {
        Gson gson = getGsonInstance();
        String json = gson.toJson(object);
        logger.trace("toJson({}): {}", object, json);
        return json;
    }

    /**
     * 주어진 Json을 역직렬화하여 객체로 획득
     * @param classOfT 획득하려는 객체 클래스, generic, collection등은 미지원, TypeToken을 이용하여 처리 가능
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = getGsonInstance();
        T object = gson.fromJson(json, classOfT);
        logger.trace("fromJson({}): {}", json, object);
        return object;
    }

    /**
     * 주어진 Json을 역직렬화하여 객체로 획득
     * @param typeOfT 획득하려는 객체 타입, Gson TypeToken을 이용하여 처리 가능
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        Gson gson = getGsonInstance();
        T object = gson.fromJson(json, typeOfT);
        logger.trace("fromJson({}): {}", json, object);
        return object;
    }

    /** 주어진 Json을 파싱하여 JsonElement로 획득 */
    public static JsonElement fromJson(String json) {
        return new JsonParser().parse(json);
    }

}
