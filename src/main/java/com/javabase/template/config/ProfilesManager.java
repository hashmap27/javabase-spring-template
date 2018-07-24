package com.javabase.template.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Spring Profile Manager
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Component
public class ProfilesManager implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(ProfilesManager.class);

    @Autowired Environment environment;

    /** 환경설정의 active profile 목록 획득 */
    public List<String> getActiveProfiles() {
        return Arrays.asList(environment.getActiveProfiles());
    }

    /** 환경설정에서 default profile 목록을 획득 */
    public List<String> getDefaultProfiles() {
        return Arrays.asList(environment.getDefaultProfiles());
    }

    /** 주어진 Profile이 Spring Profiles Active에 포함하는지 여부 */
    public boolean is(ProfileType serverProfileType) {
        return getActiveProfiles().contains(serverProfileType.name().toLowerCase());
    }

    /** 운영환경인가? */
    public boolean isProduction() { return is(ProfileType.PRODUCTION); }
    /** 개발환경인가? */
    public boolean isDevelopment() { return is(ProfileType.DEVELOPMENT); }
    /** 로컬환견인가? */
    public boolean isLocal() { return is(ProfileType.LOCAL); }

    /** 서버 Spring Profile */
    private ProfileType serverProfile = null;
    public ProfileType getServerProfile() { return serverProfile; }

    /** 서버의 Spring Profile 초기화 처리 */
    private void initServerProfile() {
        Optional<ProfileType> optionalServerProfile = EnumUtils.getEnumList(ProfileType.class)
                .stream()
                .filter(item -> is(item))
                .findFirst();
        serverProfile = optionalServerProfile.isPresent() ? optionalServerProfile.get() : null;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(
                "\r\n===============================================" +
                "\r\n ### Spring Active Profiles: " + getActiveProfiles() +
                "\r\n ### Spring Default Profiles: " + getDefaultProfiles() +
                "\r\n==============================================="
                );
        //서버 Spring Profile
        initServerProfile();
    }

}
