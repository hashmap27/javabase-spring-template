package com.javabase.template.springconfig.root;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

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
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Configuration.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Service.class),
                @ComponentScan.Filter(type=FilterType.ANNOTATION, classes = Repository.class)
        }
)
public class RootConfig {

}
