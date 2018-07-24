package com.javabase.template.framework.resttemplate;

import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javabase.template.framework.exception.TemplateRuntimeException;
import com.javabase.template.framework.fasterxml.jackson.CustomJsonObjectMapper;
import com.javabase.template.framework.fasterxml.jackson.CustomXmlObjectMapper;
import com.javabase.template.framework.http.converter.CommonMapHttpMessageConverter;
import com.javabase.template.framework.http.converter.CommonTeeHttpMessageConverter;

/**
 * RestTemplate Builder
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class RestTemplateBuilder {

    private static Logger logger = LoggerFactory.getLogger(RestTemplateBuilder.class);
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    protected RestTemplateBuilder() { /*do nothing*/ }

    /**
     * RestTemplate 빌드. (모든 SSL인증서 신뢰, Hostname 검증 안함, Converter 설정)
     */
    public static RestTemplate buildRestTemplate() {
        NoopHostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
        SSLContext sslContext = buildSSLContextDefault();

        //전역 설정
        configHttpsURLConnectionDefault(sslContext, hostnameVerifier);

        //개별 설정
        return buildRestTemplate(sslContext, hostnameVerifier);
    }

    /**
     * 주어진 SSLContext와 HostnameVerifier로 RestTemplate 빌드
     * @param sslContext
     * @param hostnameVerifier
     */
    public static RestTemplate buildRestTemplate(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            //HttpRequestFactory 설정
            HttpComponentsClientHttpRequestFactory factory = null;

            //HttpRequestFactory - SSL 설정
            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(hostnameVerifier)
                    .build();
            factory = new HttpComponentsClientHttpRequestFactory(httpClient);

            //HttpRequestFactory - Timeout 설정
            factory.setConnectTimeout(5000);            //Set the connection timeout of the underlying HttpClient
            factory.setReadTimeout(15000);              //Set the socket read timeout of the underlying HttpClient
            factory.setConnectionRequestTimeout(10000); //Set the timeout in milliseconds used when requesting a connection from the connection manager the underlying HttpClient

            restTemplate.setRequestFactory(factory);

            //Converter 설정
            configHttpMessageConverter(restTemplate);

            return restTemplate;
        } catch(Exception ex) {
            logger.warn("buildRestTemplate Error: {}", ex.toString(), logger.isTraceEnabled() ? ex : null);
            throw new TemplateRuntimeException(ex);
        }
    }

    /**
     * HttpsURLConnection의 기본 속성 설정
     */
    public static void configHttpsURLConnectionDefault(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        //전역설정
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    /**
     * 모든 인증서를 신뢰하는 SSLContext 생성. (모든 Host를 허용하고, TrustCheck를 하지 않는 TrustManager 사용)
     */
    protected static SSLContext buildSSLContextDefault() {
        try {
            TrustManager[] trustManagers = new TrustManager[] {
                    new X509TrustManager() {
                        @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        @Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {/**/}
                        @Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {/**/}
                    }
            };
            SSLContext sslContextDefault = SSLContext.getInstance("TLS");
            sslContextDefault.init(null, trustManagers, new SecureRandom());
            return sslContextDefault;
        } catch(Exception ex) {
            throw new TemplateRuntimeException(ex);
        }
    }

    /**
     * Client 인증서를 이용한 양방햔 SSL이 가능한 SSLContext 생성
     * @param clientCertClassPath 클라이언트 인증서 경로
     * @param keyPassPhrse 인증서 비밀번호
     */
    protected static SSLContext buildSSLContextForClientCert(String clientCertClassPath, String keyPassPhrse) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            ClassPathResource resource = new ClassPathResource(clientCertClassPath);
            keyStore.load(resource.getInputStream(), keyPassPhrse.toCharArray());
            return SSLContexts.custom()
                    .loadKeyMaterial(keyStore, keyPassPhrse.toCharArray())
                    .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                    .build();
        } catch(Exception ex) {
            throw new TemplateRuntimeException(ex);
        }
    }

    /**
     * RestTemplate MessageConverter 설정
     * @see org.springframework.web.client.RestTemplate Default MessageConvert
     */
    protected static void configHttpMessageConverter(RestTemplate restTemplate) {
        /*
         * javabase spring Template 기준 RestTemplate의 Default MessageConverter는 아래와 같다.
         * 1. ByteArrayHttpMessageConverter
         * 2. StringHttpMessageConverter
         * 3. ResourceHttpMessageConverter(false)
         * 4. SourceHttpMessageConverter
         * 5. AllEncompassingFormHttpMessageConverter
         * 6. MappingJackson2XmlHttpMessageConverter
         * 7. MappingJackson2HttpMEssageConverter
         * 해당 MessageConverter들은 Map<Sting, ?>에 대한 처리가 불가함.
         * RestTemplate을 간편하게 사용하기 위해 파라미터 전달 시 Map<String, ?> 사용이 빈번할 것으로 추정.
         * Map<String, ?> 변환이 가능한 MessageConverter 추가 예정.
         */

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new CommonTeeHttpMessageConverter<>(new ByteArrayHttpMessageConverter()));
        messageConverters.add(new CommonTeeHttpMessageConverter<>(new StringHttpMessageConverter()));
        messageConverters.add(new CommonTeeHttpMessageConverter<>(new ResourceHttpMessageConverter(false)));
        messageConverters.add(new CommonTeeHttpMessageConverter<>(new SourceHttpMessageConverter<>()));
        messageConverters.add(new CommonTeeHttpMessageConverter<>(new AllEncompassingFormHttpMessageConverter()));
        messageConverters.add(new CommonTeeHttpMessageConverter<>(getJsonHttpMessageConverter()));
        messageConverters.add(new CommonTeeHttpMessageConverter<>(getXmlHttpMessageConverter()));
        //Map<String, ?> 변환 처리
        messageConverters.add(new CommonTeeHttpMessageConverter<>(new CommonMapHttpMessageConverter()));

        //MessageConverter 설정처리
        restTemplate.setMessageConverters(messageConverters);
    }

    /**
     * MappingJackson2HttpMessageConverter custom 설정
     */
    protected static MappingJackson2HttpMessageConverter getJsonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter httpConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(new MediaType("application", "json", DEFAULT_CHARSET));
        mediaTypes.add(new MediaType("application", "*+json", DEFAULT_CHARSET));
        mediaTypes.add(new MediaType("text", "html", DEFAULT_CHARSET));
        httpConverter.setSupportedMediaTypes(mediaTypes);

        //ObjectMapper 설정
        ObjectMapper mapper = new CustomJsonObjectMapper();
        httpConverter.setObjectMapper(mapper);

        return httpConverter;
    }

    /**
     * MappingJackson2HttpMessageConverter custom 설정
     */
    protected static MappingJackson2XmlHttpMessageConverter getXmlHttpMessageConverter() {
        MappingJackson2XmlHttpMessageConverter httpConverter = new MappingJackson2XmlHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(new MediaType("application", "xml", DEFAULT_CHARSET));
        mediaTypes.add(new MediaType("application", "*+xml", DEFAULT_CHARSET));
        mediaTypes.add(new MediaType("text", "xml", DEFAULT_CHARSET));
        httpConverter.setSupportedMediaTypes(mediaTypes);

        //ObjectMapper 설정
        ObjectMapper mapper = new CustomXmlObjectMapper();
        httpConverter.setObjectMapper(mapper);

        return httpConverter;
    }
}
