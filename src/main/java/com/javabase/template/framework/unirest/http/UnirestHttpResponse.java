package com.javabase.template.framework.unirest.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.Body;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RawBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unirest 통신응답객체 (요청객체 및 예외정보 포함)
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnirestHttpResponse<T> {
    private static final Logger logger = LoggerFactory.getLogger(UnirestHttpResponse.class);
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /** Unirest 요청객체 */
    private BaseRequest request;

    /** 응답 상태코드 */
    private Integer statusCode;

    /** 응답 상태메시지 */
    private String statusText;

    /** 응답헤더 - 특정헤더값을 취득하기 위해서는 getHeaders.getFirst("Location") */
    private Headers headers = new Headers();

    /** 응답 Raw Body */
    private String rawBody;

    /** 응답 Body 객체(역직렬화) */
    private T body;

    /** 응답 상태코드 와 응답 상태 메시지 */
    public String getStatusCodeWithTest() {
        if(statusCode == null) {
            return null;
        }
        return "[" + statusCode + "]" + statusText;
    }

    /** 통신 중 발생한 예외정보 */
    private Exception exception;

    public UnirestHttpResponse<T> statusCode(Integer statusCode) { this.statusCode = statusCode; return this; }
    public UnirestHttpResponse<T> statusText(String statusText) { this.statusText = statusText; return this; }
    public UnirestHttpResponse<T> headers(Headers headers) { this.headers = headers; return this; }
    public UnirestHttpResponse<T> rawBody(InputStream rawBody) { this.rawBody = inputStreamToStringSafe(rawBody); return this; }
    public UnirestHttpResponse<T> body(T body) { this.body = body; return this; }
    public UnirestHttpResponse<T> exception(Exception exception) { this.exception = exception; return this; }

    /** 응답코드가 성공(2xx) 계열인가? */
    public boolean is2xxSuccessful() {
        if(statusCode == null) {
            return false;
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.is2xxSuccessful();
    }

    private UnirestHttpResponse(BaseRequest request) {
        super();
        this.request = request;
    }

    /** 성공결과 빌드 (String으로 통신한 결과에서 T로 역직렬화) */
    public static <T> UnirestHttpResponse<T> buildFromStringResponse(BaseRequest request, HttpResponse<String> response, Class<? extends T> valueType) {
        UnirestHttpResponse<T> unirestHttpResponse = new UnirestHttpResponse<>(request);
        return buildFromStringResponse(unirestHttpResponse, response, rawBody -> {
            //rawBody 역직렬화
            UnirestObjectMapper unirestObjectMapper = new UnirestObjectMapper();
            T body = unirestObjectMapper.readValue(rawBody, valueType);
            unirestHttpResponse.setBody(body);
        });
    }

    /** 성공결과 빌드 (String으로 통신한 결과에서 T로 역직렬화 - TypeReference이용) */
    public static <T> UnirestHttpResponse<T> buildFromStringResponse(BaseRequest request, HttpResponse<String> response, TypeReference<?> valueTypeRef) {
        UnirestHttpResponse<T> unirestHttpResponse = new UnirestHttpResponse<>(request);
        return buildFromStringResponse(unirestHttpResponse, response, rawBody -> {
           //rawBody 역직렬화
            UnirestObjectMapper unirestObjectMapper = new UnirestObjectMapper();
            T body = unirestObjectMapper.readValue(rawBody, valueTypeRef);
            unirestHttpResponse.setBody(body);
        });
    }
    /** 성공결과 빌드 (String으로 통신한 결과에서 T로 역직렬화 */
    public static <T> UnirestHttpResponse<T> buildFromStringResponse(UnirestHttpResponse<T> unirestHttpResponse, HttpResponse<String> response, Consumer<String> consumerFunction) {
        if(response != null) {
            unirestHttpResponse.setStatusCode(response.getStatus());
            unirestHttpResponse.setStatusText(response.getStatusText());
            unirestHttpResponse.setHeaders(response.getHeaders());
            unirestHttpResponse.setRawBody(inputStreamToStringSafe(response.getRawBody()));
            //응답코드에 따른 처리
            HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
            if(httpStatus.is2xxSuccessful()) {
                //성공 응답
                String rawBody = unirestHttpResponse.getRawBody();
                if(StringUtils.isEmpty(rawBody)) {
                    unirestHttpResponse.setBody(null);
                } else {
                    //rawBody 역직렬화
                    consumerFunction.accept(rawBody);
                }
            } else {
                //오류 응답
            }
        }
        return unirestHttpResponse;
    }

    /** 성공결과 빌드 (asObject로 바로 T타입의 Response로 빌드) */
    public static <T> UnirestHttpResponse<T> build(BaseRequest request, HttpResponse<T> response) {
        UnirestHttpResponse<T> unirestHttpResponse = new UnirestHttpResponse<>(request);
        if(response != null) {
            unirestHttpResponse.setStatusCode(response.getStatus());
            unirestHttpResponse.setStatusText(response.getStatusText());
            unirestHttpResponse.setHeaders(response.getHeaders());
            unirestHttpResponse.setRawBody(inputStreamToStringSafe(response.getRawBody()));
            unirestHttpResponse.setBody(response.getBody());
        }
        return unirestHttpResponse;
    }

    /** Exception 결과 빌드 */
    public static <T> UnirestHttpResponse<T> buildException(BaseRequest request, Exception exception) {
        UnirestHttpResponse<T> unirestHttpResponse = new UnirestHttpResponse<>(request);
        unirestHttpResponse.setException(exception);
        return unirestHttpResponse;
    }

    /** InputStream을 문자열로 읽기 (null 또는 예외 시 "") */
    public static String inputStreamToStringSafe(InputStream input) {
        try {
            if(input == null) {
                return "";
            }
            return StringUtils.stripEnd(IOUtils.toString(input, UTF8), "\r\n");
        } catch(IOException ex) {
            logger.debug("{} - {}", ex.getClass().getName(), ex.getMessage(), logger.isTraceEnabled() ? ex : null);
            return "";
        }
    }

    /** 요청객체의 로깅문자열 획득 */
    private static String getRequestLogString(BaseRequest request) {
        HttpRequest httpRequest = request.getHttpRequest();
        if(httpRequest == null) {
            return request.toString();
        }
        String logString = request.getClass().getSimpleName() + " ["
                + "method= " + httpRequest.getHttpMethod() + ", "
                + "url= " + httpRequest.getUrl() + ", "
                + "headers= " + httpRequest.getHeaders();
        if(httpRequest.getBody() != null) {
            logString += ", body= " + getRequestBodyLogString(httpRequest.getBody());
        }
        logString += "]";
        return logString;
    }

    /** 요청객체 Body로깅문자열 획득 */
    private static String getRequestBodyLogString(Body body) {
        if(body instanceof RequestBodyEntity) {
            RequestBodyEntity bodyEntity = (RequestBodyEntity) body;
            return bodyEntity.getBody().toString();
        } else if(body instanceof MultipartBody) {
            return body.toString();
        } else if(body instanceof RawBody) {
            RawBody rawBody = (RawBody) body;
            byte[] bodyBytes = (byte[])rawBody.getBody();
            return new String(bodyBytes, UTF8);
        }
        return body.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UnirestHttpResponse ["
                + "status=[" + statusCode + "]" + statusText + ","
                + "header=" + headers + ", ");
        if(body != null) {
            sb.append("body=" + body + ", ");
        } else {
            sb.append("rawbody=" + rawBody + ", ");
        }
        sb.append("request=" + getRequestLogString(request) + ", "
                + "exception=" + exception + "]");
        return sb.toString();

    }
}
