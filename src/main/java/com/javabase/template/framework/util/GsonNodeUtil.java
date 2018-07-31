package com.javabase.template.framework.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.javabase.template.framework.exception.TemplateRuntimeException;

/**
 * Gson의 JsonElement Node Util
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class GsonNodeUtil {
    private static final Logger logger = LoggerFactory.getLogger(GsonNodeUtil.class);

    private GsonNodeUtil() { /* do nothing */ }

    /** JsonElement가 null인경우 빈문자열 처리 */
    public static String getAsStringSafe(JsonElement item) {
        if(item == null || item.isJsonNull())
            return "";
        else
            return item.getAsString();
    }

    /** JsonElement가 null인경우 0 처리 */
    public static int getAsIntSafe(JsonElement item) {
        if(item == null || item.isJsonNull())
            return 0;
        else
            return item.getAsInt();
    }

    /**
     * 주어진 노드에서 자식 노드 JsonElement를 획득 (없으면 null 반환)
     * @param node 부모노드
     * @param childNodeName 자식노드명(대소문자무시 검색)
     * @return 자식 노드 JsonElement를 획득
     */
    public static JsonElement getChildNodeElementSafe(JsonElement node, String childNodeName) {
        if(node == null || node.isJsonNull()) {
            logger.debug("getChildNodeElementSafe FAILED. node is {}. cannot find childNode {}", node, childNodeName);
            return null;
        }
        Set<Entry<String, JsonElement>> entrySet = node.getAsJsonObject().entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            if(!entry.getValue().isJsonNull() && entry.getKey().compareToIgnoreCase(childNodeName) == 0) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 주어진 노드에서 자식 노드 JsonElement를 획득 (없으면 예외 발생)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드 JsonElement를 획득
     */
    public static JsonElement getChildNodeElement(JsonElement node, String childNodeName) {
        JsonElement childNodeElement = getChildNodeElementSafe(node, childNodeName);
        if(childNodeElement != null) {
            return childNodeElement;
        }
        throw new TemplateRuntimeException(String.format("Cannot found childNode %s at %s", childNodeName, node));
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 String으로 획득 (없으면 "")
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 String으로 획득
     */
    public static String getChildNodeAsStringSafe(JsonElement node, String childNodeName) {
        return getAsStringSafe(getChildNodeElementSafe(node, childNodeName));
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 String으로 획득 (없으면 오류)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 String으로 획득
     */
    public static String getChildNodeAsString(JsonElement node, String childNodeName) {
        return getChildNodeElement(node, childNodeName).getAsString();
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 String으로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본 문자열 값
     * @return 자식 노드의 값을 String으로 획득
     */
    public static String getChildNodeAsString(JsonElement node, String childNodeName, String defaultValue) {
        return getChildNodeString(node, childNodeName, defaultValue, false/*silent*/);
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 String으로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본 문자열 값
     * @return 자식 노드의 값을 String으로 획득
     */
    public static String getChildNodeString(JsonElement node, String childNodeName, String defaultValue, boolean silent) {
        JsonElement child = getChildNodeElementSafe(node, childNodeName);
        if(child==null || child.isJsonNull()) {
            if(!silent) {
                logger.debug("Cannot found childNode {}. defaultValue: '{}'", childNodeName, defaultValue);
            }
            return defaultValue;
        }
        return child.getAsString();
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Integer로 획득 (없으면 오류)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 Integer로 획득
     */
    public static int getChildNodeAsInt(JsonElement node, String childNodeName) {
        JsonElement child = getChildNodeElement(node, childNodeName);
        try {
            return child.getAsInt();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Cannot parse to Integer childNode %s value %s", childNodeName, child.getAsString()));
        }
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Integer로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본 Integer 값
     * @return 자식 노드의 값을 Integer로 획득
     */
    public static int getChildNodeInt(JsonElement node, String childNodeName, int defaultValue) {
        JsonElement child = getChildNodeElementSafe(node, childNodeName);
        if(child==null || child.isJsonNull()) {
            return defaultValue;
        }
        try {
            return child.getAsInt();
        } catch (NumberFormatException ex) {
            logger.debug("childNode {} value: {} - NumberFormatException Occured. -> return defaultValue: {}", childNodeName, child.getAsString(), defaultValue);
            return defaultValue;
        }
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Long로 획득 (없으면 오류)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 Long로 획득
     */
    public static long getChildNodeAsLong(JsonElement node, String childNodeName) {
        JsonElement child = getChildNodeElement(node, childNodeName);
        try {
            return child.getAsLong();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Cannot parse to Long childNode %s value %s", childNodeName, child.getAsString()));
        }
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Long로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본 Long 값
     * @return 자식 노드의 값을 Long로 획득
     */
    public static long getChildNodeLong(JsonElement node, String childNodeName, long defaultValue) {
        JsonElement child = getChildNodeElementSafe(node, childNodeName);
        if(child==null || child.isJsonNull()) {
            return defaultValue;
        }
        try {
            return child.getAsLong();
        } catch (NumberFormatException ex) {
            logger.debug("childNode {} value: {} - NumberFormatException Occured. -> return defaultValue: {}", childNodeName, child.getAsString(), defaultValue);
            return defaultValue;
        }
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 boolean로 획득 (없으면 오류)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 boolean으로 획득
     */
    public static boolean getChildNodeAsBool(JsonElement node, String childNodeName) {
        String value = getChildNodeAsStringSafe(node, childNodeName);
        if(StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Invalid " + childNodeName + " value: " + value);
        }
        Boolean booleanValue = ConvertUtil.toBoolean(value);
        if(booleanValue == null) {
            throw new IllegalArgumentException("Invalid " + childNodeName + " value: " + value);
        }
        return booleanValue;
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 boolean로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본값
     * @return 자식 노드의 값을 boolean으로 획득
     */
    public static boolean getChildNodeAsBool(JsonElement node, String childNodeName, boolean defaultValue) {
        String value = getChildNodeAsStringSafe(node, childNodeName);
        if(StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return ConvertUtil.toBool(value, defaultValue);
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Boolean로 획득 (없으면 NULL)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 Boolean으로 획득
     */
    public static Boolean getChildNodeAsBoolean(JsonElement node, String childNodeName) {
        String value = getChildNodeAsStringSafe(node, childNodeName);
        if(StringUtils.isEmpty(value)) {
            return null;
        }
        return ConvertUtil.toBoolean(value);
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Boolean로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본값
     * @return 자식 노드의 값을 Boolean으로 획득
     */
    public static Boolean getChildNodeAsBool(JsonElement node, String childNodeName, Boolean defaultValue) {
        String value = getChildNodeAsStringSafe(node, childNodeName);
        if(StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return ConvertUtil.toBoolean(value, defaultValue);
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Enum으로 변환하여 획득 (없으면 예외 발생)
     * @param node          부모노드
     * @param childNodeName 자식노드명
     * @param enumType      반환할 EnumType
     * @return              자식 노드의 값을 Enum으로 변환하여 획득
     */
    protected <T extends Enum<T>> T getChildNodeAsEnum(JsonElement node, String childNodeName, Class<T> enumType) {
        String value = getChildNodeAsString(node, childNodeName);
        T enumValue = EnumUtils.getEnum(enumType, value);
        if(enumValue == null) {
            String message = "Cannot found enum value '" + value + "' at " + enumType.getName();
            throw new TemplateRuntimeException(message);
        }
        return enumValue;
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 Enum으로 변환하여 획득 (없으면 기본값 반환)
     * @param node          부모노드
     * @param childNodeName 자식노드명
     * @param enumType      반환할 EnumType
     * @param defaultValue  자식노드가 JsonNull일 경우 리턴할 기본 값
     * @return              자식 노드의 값을 Enum으로 변환하여 획득
     */
    protected <T extends Enum<T>> T getChildNodeAsEnum(JsonElement node, String childNodeName, Class<T> enumType, T defaultValue) {
        String value = getChildNodeAsString(node, childNodeName, "");
        if(StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        T enumValue = EnumUtils.getEnum(enumType, value);
        if(enumValue == null) {
            logger.debug("Cannot found enum value '{}' at {}", value, enumType.getName());
            return defaultValue;
        }
        return enumValue;
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 JsonArray로 획득 (없으면 오류)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 JsonArray로 획득
     */
    public static JsonArray getChildNodeAsJsonArray(JsonElement node, String childNodeName) {
        return getChildNodeElement(node, childNodeName).getAsJsonArray();
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 JsonArray로 획득 (없으면 빈 JsonArray)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값을 JsonArray로 획득
     */
    public static JsonArray getChildNodeAsJsonArraySafe(JsonElement node, String childNodeName) {
        return getChildNodeAsJsonArray(node, childNodeName, new JsonArray());
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 JsonArray로 획득 (없으면 기본값)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @param defaultValue 자식노드가 JsonNull일 경우 리턴할 기본 값
     * @return 자식 노드의 값을 JsonArray로 획득
     */
    public static JsonArray getChildNodeAsJsonArray(JsonElement node, String childNodeName, JsonArray defaultValue) {
        JsonElement child = getChildNodeElementSafe(node, childNodeName);
        if(child==null || child.isJsonNull()) {
            logger.debug("Cannot found childNode {}. defaultValue: {}", childNodeName, defaultValue);
            return defaultValue;
        }
        return child.getAsJsonArray();
    }

    /**
     * 주어진 노드에서 자식 노드의 값인 JsonArray를 String으로 획득 (없으면 예외 발생)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return  자식 노드의 값인 JsonArray를 String으로 획득
     */
    protected String getChildNodeAsJsonArrayString(JsonElement node, String childNodeName) {
        JsonArray jsonArray = getChildNodeAsJsonArray(node, childNodeName);
        return jsonArray.getAsString();
    }

    /**
     * 주어진 노드에서 자식 노드의 값인 JsonArray를 String으로 획득 (비거나 없으면 Null 반환)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값인 JsonArray를 String으로 획득
     */
    protected String getChildNodeAsArrayString(JsonElement node, String childNodeName) {
        JsonArray jsonArray = getChildNodeAsJsonArraySafe(node, childNodeName);
        if(jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.getAsString();
    }

    /**
     * 주어진 노드에서 자식 노드의 값인 JsonArray를 String List로 획득 (없으면 예외 발생)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값인 JsonArray를 String List로 획득
     */
    public static List<String>  getChildNodeAsJsonArrayStringList(JsonElement node, String childNodeName) {
        JsonArray jsonArray = getChildNodeAsJsonArray(node, childNodeName);
        return convertJsonArrayToListString(jsonArray);
    }

    /**
     * 주어진 노드에서 자식 노드의 값인 JsonArray를 String List로 획득 (비거나 없으면 빈 컬렉션 반환)
     * @param node 부모노드
     * @param childNodeName 자식노드명
     * @return 자식 노드의 값인 JsonArray를 String List로 획득
     */
    public static List<String> getChildNodeAsJsonArrayStringListSafe(JsonElement node, String childNodeName) {
        JsonArray jsonArray = getChildNodeAsJsonArraySafe(node, childNodeName);
        return convertJsonArrayToListString(jsonArray);
    }

    /**
     * JsonArray를 String List로 획득
     * @param jsonArray 변환하고 싶은 JsonArray
     */
    protected static List<String> convertJsonArrayToListString(JsonArray jsonArray) {
        if(jsonArray == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            if(jsonElement == null || jsonElement.isJsonNull()) {
                result.add(null);
            } else {
                result.add(jsonElement.getAsString());
            }
        }
        return result;
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 주어진 타입 객체로 획득 (없으면 예외 발생)
     * @param node          부모노드
     * @param childNodeName 자식노드명
     * @param typeToken     얻고 싶은 객체 타입
     * @return              주어진 노드값을 주어진 타입 객체로 획득
     */
    protected <T> T getChildNodeAsObject(JsonElement node, String childNodeName, TypeToken<T> typeToken) {
        JsonElement child = getChildNodeElement(node, childNodeName);
        return GsonUtil.getGsonInstance().fromJson(child, typeToken.getType());
    }

    /**
     * 주어진 노드에서 자식 노드의 값을 주어진 타입 객체로 획득 (없으면 기본값 반환)
     * @param node          부모노드
     * @param childNodeName 자시노드명
     * @param typeToken     얻고 싶은 객체 타입
     * @param defaultValue  자식노드가 JsonNull일 경우 리턴할 기본 값
     * @return              주어진 노드의 값을 주어진 타입 객체로 획득
     */
    protected <T> T getChildNodeAsObject(JsonElement node, String childNodeName, TypeToken<T> typeToken, T defaultValue) {
        JsonElement child = getChildNodeElementSafe(node, childNodeName);
        if(child == null || child.isJsonNull()) {
            return defaultValue;
        }
        return GsonUtil.getGsonInstance().fromJson(child, typeToken.getType());
    }

}
