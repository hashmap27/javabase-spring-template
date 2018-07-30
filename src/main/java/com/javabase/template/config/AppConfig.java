package com.javabase.template.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import lombok.Data;

@Data
public class AppConfig implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    private static AppConfig instance;
    public static AppConfig getInstance() {
        return instance;
    }

    @Value("${jdbc.driverClass:}")
    private String jdbcDriverClass;
    @Value("${jdbc.url:}")
    private String jdbcUrl;
    @Value("${jdbc.username:}")
    private String jdbcUsername;
    @Value("${jdbc.password:}")
    private String jdbcPassword;

    @Autowired Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {
        //Holder설정 (static에서 접근 시 사용)
        AppConfig.instance = this;

        //Validation
        Assert.notNull(jdbcUrl, "jdbc.url 설정은 필수입니다.");

        //로그 출력
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n========================================================");
        sb.append("\r\n[AppConfiguration]");
        sb.append("\r\n- spring.profiles.active: " + Arrays.asList(environment.getActiveProfiles()));
        sb.append("\r\n");
        sb.append("\r\n- jdbc.driverName: " + jdbcDriverClass);
        sb.append("\r\n- jdbc.url: " + jdbcUrl);
        sb.append("\r\n- jdbc.username: " + jdbcUsername);
        //필요 Configuration 설정 시 add하기.
        sb.append("\r\n========================================================");
        logger.info(sb.toString());
    }
}
