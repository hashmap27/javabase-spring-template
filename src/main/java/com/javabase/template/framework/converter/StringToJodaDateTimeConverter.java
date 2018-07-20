package com.javabase.template.framework.converter;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.javabase.template.framework.exception.TemplateRuntimeException;
import com.javabase.template.framework.util.DateUtil;

/**
 * 문자열과 Joda DateTime형의 변환
 * 여러 DateTime형의 문자열을 DateTime으로 변환
 * WebMvcConfigure 의 addFormatters에 추가 반영 필요.
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class StringToJodaDateTimeConverter implements Converter<String, DateTime> {

    private final Logger logger = LoggerFactory.getLogger(StringToJodaDateTimeConverter.class);

    @Override
    public DateTime convert(String dateString) {
        try {
            logger.trace("converter: {}", dateString);
            return DateUtil.parseToDateTime(dateString);
        } catch(Exception ex) {
            logger.trace("converter({}) - Exception: {}", dateString, ex.toString());
            throw new TemplateRuntimeException("Cannot converter dateString to Joda DateTime", ex);
        }
    }

}
