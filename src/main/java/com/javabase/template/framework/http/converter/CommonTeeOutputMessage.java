package com.javabase.template.framework.http.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.util.StringUtils;

/**
 * HttpOutputMessage를 처리하여 출력내용을 내부 ByteArray에 저장하여 출력등에 사용할 수 있는 유틸
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class CommonTeeOutputMessage implements HttpOutputMessage {

    HttpOutputMessage httpOutputMessage;
    OutputStream customBodySteam;
    ByteArrayOutputStream byteArrayOutputSteam;

    public CommonTeeOutputMessage(HttpOutputMessage httpOutputMessage) throws IOException {
        this.httpOutputMessage = httpOutputMessage;
        byteArrayOutputSteam = new ByteArrayOutputStream();
        customBodySteam = new TeeOutputStream(httpOutputMessage.getBody(), byteArrayOutputSteam);
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpOutputMessage.getHeaders();
    }

    @Override
    public OutputStream getBody() throws IOException {
        return customBodySteam;
    }


    /**
     * 출력 내용 Bytes
     */
    public byte[] getTeeOutputByte() {
        return this.byteArrayOutputSteam.toByteArray();
    }

    /**
     * 출력 내용 String(UTF-8)
     * @throws UnsupportedEncodingException
     */
    public String getTeeOutputString() throws UnsupportedEncodingException {
        return StringUtils.trimTrailingWhitespace(this.byteArrayOutputSteam.toString("UTF-8"));
    }

}
