package com.javabase.template.framework.http.converter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * HttpMessageConverter를 래핑하여 입출력 내용을 로깅하는 클래스
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class CommonTeeHttpMessageConverter<T> implements HttpMessageConverter<T> {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    HttpMessageConverter<T> httpMessageConverter = null;

    public CommonTeeHttpMessageConverter(HttpMessageConverter<T> httpMessageConverter) {
        this.httpMessageConverter = httpMessageConverter;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return httpMessageConverter.canRead(clazz, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return httpMessageConverter.canWrite(clazz, mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return httpMessageConverter.getSupportedMediaTypes();
    }

    @Override
    public T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
        CommonTeeHttpInputMessage teeHttpInputMessage = new CommonTeeHttpInputMessage(inputMessage);
        T result = httpMessageConverter.read(clazz, teeHttpInputMessage);
        logger.debug("\r\n  >>>> READ: {}", teeHttpInputMessage.getTeeInputString());
        return result;
    }

    @Override
    public void write(T t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        CommonTeeOutputMessage teeHttpOutputMessage = new CommonTeeOutputMessage(outputMessage);
        httpMessageConverter.write(t, contentType, teeHttpOutputMessage);
        logger.debug("\r\n  >>>> WRITE: {}", teeHttpOutputMessage.getTeeOutputString());
    }

}
