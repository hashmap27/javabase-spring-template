package com.javabase.template.springconfig;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.joda.time.DateTime;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
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
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        //로그 출력
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n=====================================");
        sb.append("\r\n- JavaBase Spring Configuration Template Start Up");
        sb.append("\r\n- " + DateTime.now());
        sb.append("\r\n=====================================");
        logger.debug(sb.toString());
    }

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

    @Override
    protected Filter[] getServletFilters() {
        //NOTE: Dispatcher Servlet 필터 등록
        return new Filter[] {
                new ShallowEtagHeaderFilter(),
                getCharacterEncodingFilter()
        };
    }

    //한글깨짐 방지 - UTF-8 인코딩
    protected CharacterEncodingFilter getCharacterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

}
