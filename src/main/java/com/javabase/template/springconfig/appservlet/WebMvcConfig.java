package com.javabase.template.springconfig.appservlet;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MappingJackson2XmlView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javabase.template.framework.converter.StringToJodaDateTimeConverter;
import com.javabase.template.framework.converter.StringToJodaLocalDateTimeConverter;

/**
 * servlet-context.xml의 역할을 대신하거나 보충하는 클래스
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = "com.javabase",
        useDefaultFilters = false,
        excludeFilters = {
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Configuration.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Service.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Repository.class)
        },
        includeFilters = {
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Controller.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = RestController.class)
        }
)
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired @Qualifier(value="jsonMapper") ObjectMapper jsonMapper;
    @Autowired @Qualifier(value="xmlMapper") ObjectMapper xmlMapper;

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(false)                        //HttpReqeust Header의 Accept 무시여부
                    .favorPathExtension(true)                       //프로퍼티 값을 보고 URL의 확장자에서 리턴 포맷을 결정 여부
                    .ignoreUnknownPathExtensions(true)              //모든 미디어 유형으로 해결할 수 없는 경로 확장자를 가진 요청을 무시할지 여부
                    .favorParameter(true)                           //URL호출 시 특정 파라미터로 리턴포맷 전달 허용 여부
                    .mediaType("xml", MediaType.APPLICATION_XML)
                    .mediaType("json", MediaType.APPLICATION_JSON)
                    .defaultContentType(MediaType.TEXT_HTML);
    }

    /** Spring Formatter */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        //Deserialize시에 사용할 Formatter
        //String To Joda Date Converting added.
        //Serialize시에는 다른 설정이 필요함.
        registry.addConverter(new StringToJodaDateTimeConverter());
        registry.addConverter(new StringToJodaLocalDateTimeConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //NOTE: 입맛대로 수정
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/").setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).mustRevalidate());
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).mustRevalidate());
        registry.addResourceHandler("/html/**").addResourceLocations("/html").setCacheControl(CacheControl.noStore());
        registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).mustRevalidate());
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCacheControl(CacheControl.noStore());
        registry.addResourceHandler("/lib/**").addResourceLocations("/lib/").setCacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).mustRevalidate());
        registry.addResourceHandler("/web/**").addResourceLocations("/web/").setCacheControl(CacheControl.noStore());
    }

    /**
     * Cross-Origin Resource Sharing
     * 웹페이지에서 ajax를 사용할 때 다른 도메인의 서버 리소스(JSON 등)에 접근하기 위한 메커니즘.
     * 자세한 내용은 W3C 홈페이지에서 확인 - http://www.w3.org/TR/cors/
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  //CORS기능 사용 경로 지정(ROOT)
                .allowedOrigins("*")    //접근 허용 Origin(도메인) 지정. 기본적으로 무제한이라는 의미로 '*'이 적용
                .allowedMethods("GET", "POST", "PUT", "DELETE")     //접근을 허용할 HTTP 메소드 지정. default: GET, HEAD, POST가 하용.
                .allowCredentials(false)    //인증정보(쿠키 등)를 취급할지 여부 결정 default: true
                .maxAge(3600);   //클라이언트가 요청에 대한 응답을 캐시할 시간(초 단위)을 지정. default: 1800(30분
    }

    /** Spring에서 사용할 ViewResolver 설정 */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setPrettyPrint(true);
        MappingJackson2XmlView xmlView = new MappingJackson2XmlView();
        xmlView.setPrettyPrint(true);
        registry.enableContentNegotiation(jsonView, xmlView);
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    /**
     * 요청본문을 자바 객체로 변환하고 자바객체를 응답본문으로 변환 시 MessageConverter가 사용.
     * MessageConvertor 구현 클래스 설정
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //바이트 배열 변환용 클래스. 바이트 배열 읽기/쓰기, 모든 미디어 타입(*/*)에서 읽고 appliaction-octect-stream으로 쓴다.
        converters.add(new ByteArrayHttpMessageConverter());

        //String 변환용 클래스. 모든 미디어 타입(*/*)을 String으로 읽고 text/plain에 대한 String을 쓴다.
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(true);
        converters.add(stringHttpMessageConverter);

        //Resource를 읽고 쓰기.
        converters.add(new ResourceHttpMessageConverter());

        //Form 또는 MultiPart형식 읽고 쓰기. application-x-www-form-urlencoded.
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        List<MediaType> supportedMediaTypesOfForm = formHttpMessageConverter.getSupportedMediaTypes();
        List<MediaType> newSupportedMediaTypesOfForm = new ArrayList<>();
        newSupportedMediaTypesOfForm.addAll(supportedMediaTypesOfForm);
        formHttpMessageConverter.setSupportedMediaTypes(newSupportedMediaTypesOfForm);
        converters.add(formHttpMessageConverter);

        //FasterXML Jackson Databind를 이용한 JSON미디어 타입을 자바객체로 변환시 사용하는 클래스
        MappingJackson2HttpMessageConverter json = new MappingJackson2HttpMessageConverter(jsonMapper);
        //IE9에서 json을 iframe transport로 전송 시 파일로 저장하려는 버그 발생 -> text 미디어타입으로 처리
        List<MediaType> supportedMediaTypes = json.getSupportedMediaTypes();
        List<MediaType> newSupportedMediaTypes = new ArrayList<>();
        newSupportedMediaTypes.addAll(supportedMediaTypes);
        newSupportedMediaTypes.add(MediaType.TEXT_HTML);
        json.setSupportedMediaTypes(newSupportedMediaTypes);
        json.setPrettyPrint(true);
        converters.add(json);

        //FasterXML Jackson XML Databind를 이용한 XML 미디어 타입을 자바객체로 변환시 사용하는 클래스
        MappingJackson2XmlHttpMessageConverter xml = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
        xml.setPrettyPrint(true);
        converters.add(xml);
    }

    /** 파일업로드 */
    @Bean(name="multipartResolver") //Spring default bean name
    public CommonsMultipartResolver commonsMultipartFile() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        //NOTE: 파일 업로드 제한 가능.
        //resolver.setMaxUploadSize(-1);    //파일 하나의 최대 바이트 수를 지정. default: -1(제한 없음)
        //if(!StringUtils.isEmpty(appConfig.getUploadTempDir()) {   //업로드 파일 임시 저장 경로
        //    FileSystemResource resource = new FileSystemResource(appConfig.getUploadTempDir());
        //    resolver.setUploadTempDir(resource);
        //}
        return resolver;
    }

}
