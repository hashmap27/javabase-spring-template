package com.javabase.template.framework.converter;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.javabase.template.framework.exception.TemplateRuntimeException;
import com.javabase.template.framework.util.DateUtil;

/**
 * 문자열과 Joda LocalDateTime형의 변환
 * 여러 LocalDateTime형의 문자열을 LocalDateTime으로 변환
 * WebMvcConfigurer 의 addFormatters에 추가해야 반영됨.
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class StringToJodaLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private final Logger logger = LoggerFactory.getLogger(StringToJodaLocalDateTimeConverter.class);

    @Override
    public LocalDateTime convert(String dateString) {
        try {
            logger.trace("convert({})", dateString);
            return DateUtil.parseToLocalDateTime(dateString);
        } catch(Exception ex) {
            logger.trace("convert({}) - Exception: {}", dateString, ex.toString());
            throw new TemplateRuntimeException("Cannot convert dateString to Joda LocalDateTime", ex);
        }
    }

}
