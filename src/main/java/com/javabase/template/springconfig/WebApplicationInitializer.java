package com.javabase.template.springconfig;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.javabase.template.springconfig.appservlet.WebMvcConfig;
import com.javabase.template.springconfig.root.RootConfig;

/**
 * web.xml의 역할을 대신하거나 보충하는 클래스
 *
 * @author Choeng SungHyun <hashmap27@gmail.com>
 */
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?> [] {
            RootConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?> [] {
            WebMvcConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

}
