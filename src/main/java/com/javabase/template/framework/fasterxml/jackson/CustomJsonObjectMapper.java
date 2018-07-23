package com.javabase.template.framework.fasterxml.jackson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * 공통 Jackson2 Object Mapper
 *
 *   - JodaModule 등록
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class CustomJsonObjectMapper  extends ObjectMapper {
    private static final long serialVersionUID = 5297101137810467554L;

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSZ";

    public CustomJsonObjectMapper() {
        this(DEFAULT_DATETIME_FORMAT);
    }

    public CustomJsonObjectMapper(String simpleDateTimeFormat) {
        ObjectMapper objectMapper = new ObjectMapper();
        //JodaModule 등록처리
        objectMapper.registerModule(new JodaModule());

        //NOTE: Features는 소스 참고. org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean
        //Features 설정
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);    //Pretty Print
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION); //@JsonView의 사용을 위해 기본포함기능을 비활성화.
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);    //Deserialization에서 알려지지 않은 속성에 대해 오류 방지
        //TimeZone 설정 - Jackson은 기본적으로 GMT를 사용.
        objectMapper.setTimeZone(TimeZone.getDefault());    //Java Default Time

        //Default DateTime Format 지정
        if(simpleDateTimeFormat != null) {
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            DateFormat dateFormat = new SimpleDateFormat(simpleDateTimeFormat);
            objectMapper.setDateFormat(dateFormat);
        }
    }

}
