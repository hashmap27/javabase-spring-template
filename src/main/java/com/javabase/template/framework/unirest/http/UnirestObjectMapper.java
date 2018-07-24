package com.javabase.template.framework.unirest.http;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.javabase.template.framework.exception.TemplateRuntimeException;
import com.mashape.unirest.http.ObjectMapper;

/**
 * Unirest용 ObjectMapper 설정. 내부에서 Jackson의 ObjectMapper에 위임
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class UnirestObjectMapper implements ObjectMapper {

    private com.fasterxml.jackson.databind.ObjectMapper jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public com.fasterxml.jackson.databind.ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    public UnirestObjectMapper() {
        jsonMapper.setSerializationInclusion(Include.NON_NULL);
        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jsonMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        try {
            if(valueType.equals(String.class)) {
                return (T)value;
            }
            return jsonMapper.readValue(value, valueType);
        } catch(IOException ex) {
            throw new TemplateRuntimeException(ex);
        }
    }

    public <T> T readValue(String value, TypeReference<?> valueTypeRef) {
        try {
            return jsonMapper.readValue(value, valueTypeRef);
        } catch(IOException ex) {
            throw new TemplateRuntimeException(ex);
        }
    }

    @Override
    public String writeValue(Object value) {
        try {
            return jsonMapper.writeValueAsString(value);
        } catch(JsonProcessingException ex) {
            throw new TemplateRuntimeException(ex);
        }
    }

}
