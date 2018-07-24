package com.javabase.template.framework.http.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.input.TeeInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StringUtils;

/**
 * HttpInputMessage를 처리하여 입력 내용을 내부 ByteArray에 저장하여 출력등에 사용할 수 있는 유틸
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class CommonTeeHttpInputMessage implements HttpInputMessage {

    HttpInputMessage httpInputMessage;
    InputStream customBodyStream;
    ByteArrayOutputStream byteArrayOutputStream;

    public CommonTeeHttpInputMessage(HttpInputMessage httpInputMessage) throws IOException {
        this.httpInputMessage = httpInputMessage;
        byteArrayOutputStream = new ByteArrayOutputStream();
        customBodyStream = new TeeInputStream(httpInputMessage.getBody(), byteArrayOutputStream);
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpInputMessage.getHeaders();
    }

    @Override
    public InputStream getBody() throws IOException {
        return customBodyStream;
    }

    /**
     * 입력 내용 Byte
     */
    public byte[] getTeeInputByte() {
        return this.byteArrayOutputStream.toByteArray();
    }

    /**
     * 입력 내용 String(UTF-8)
     * @throws UnsupportedEncodingException
     */
    public String getTeeInputString() throws UnsupportedEncodingException {
        return StringUtils.trimTrailingWhitespace(this.byteArrayOutputStream.toString("UTF-8"));
    }

}
