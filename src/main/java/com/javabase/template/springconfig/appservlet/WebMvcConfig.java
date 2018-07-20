package com.javabase.template.springconfig.appservlet;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MappingJackson2XmlView;

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

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setPrettyPrint(true);
        MappingJackson2XmlView xmlView = new MappingJackson2XmlView();
        xmlView.setPrettyPrint(true);
        registry.enableContentNegotiation(jsonView, xmlView);
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

}
