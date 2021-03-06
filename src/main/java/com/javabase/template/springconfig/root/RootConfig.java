package com.javabase.template.springconfig.root;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javabase.template.config.AppConfig;
import com.javabase.template.framework.fasterxml.jackson.CustomJsonObjectMapper;
import com.javabase.template.framework.fasterxml.jackson.CustomXmlObjectMapper;

/**
 * root-context.xml의 역할을 대신하거나 보충하는 클래스
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Configuration
@ComponentScan(
        basePackages = "com.javabase",
        useDefaultFilters = false,
        excludeFilters = {
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Controller.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = RestController.class)
        },
        includeFilters = {
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Component.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Configuration.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Service.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Repository.class)
        }
)
@PropertySource("classpath:/runtimeEnv/application-${spring.profiles.active}.properties")
public class RootConfig {

    /** Application 설정 접근 Util */
    @Bean
    public AppConfig appConfig() {
        return new AppConfig();
    }

    /** MappingJackson ObjectMapper */
    @Bean(name="jsonMapper")
    public ObjectMapper jsonMapper() {
        return new CustomJsonObjectMapper();
    }

    /** MappingJackson XmlMapper */
    @Bean(name="xmlMapper")
    public ObjectMapper xmlMapper() {
        return new CustomXmlObjectMapper();
    }
}
