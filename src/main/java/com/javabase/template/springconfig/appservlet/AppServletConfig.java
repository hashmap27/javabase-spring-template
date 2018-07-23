package com.javabase.template.springconfig.appservlet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.javabase.template.framework.web.log.ControllerLoggingAspect;

/**
 * Spring 관련 컴포넌트가 아닌 서비스에서 소비하는 컴포넌트에 대한 설정
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Configuration
public class AppServletConfig {

    @Bean
    public ControllerLoggingAspect controllerLoggingAspect() {
        return new ControllerLoggingAspect();
    }
}
