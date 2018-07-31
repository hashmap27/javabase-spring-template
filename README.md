# Javabase Spring Template #


### 이 저장소는? ###

* Javabase Spring Template으로 Spring boot 기반 사용을 하지 않고 Spring Framework를 구성하고자 할때 사용하기 위한 기본 Template


### 설정방법? ###

* 필요사항
    * Maven 프로젝트
    * lombok 사용
    * Java: jdk 1.8
    * WAS: Tomcat 9.0
    * MySQL 사용

* 주요 사항
    * web.xml을 사용하지 않음.
    * root-context.xml, servlet-context.xml을 모두 Javabase로 구현하였음.
    * xml 방식은 Spring 재단에서도 지양하고 있으며 학습곡선이 매우 큼
    * Javabase기반 방식은 컴파일 단계에서 에러가 발견되기 때문에

```
<!-- web.xml 제거를 위해 추가. -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>2.6</version>
    <configuration>
        <failOnMissingWebXml>false</failOnMissingWebXml>
    </configuration>
</plugin>
```

* 주요 종속성
    * Spring framework: 5.0.7.RELEASE
    * AspectJ: 1.9.1
    * Logback: 1.2.3
    * apache - httpcore, httpclient, httpasyncclient, httpmime, commons-lang3, commons-io, commons-fileupload
    * Joda Time
    * Jackson - core, databind, annotations, datatype-joda, dataformat-xml, datatype-guava
    * google - gson, guava
    * mybatis
    * Unirest: 1.4.9

* Database configuration
    * Local DB
        * Server : localhost
        * Port : 3306
        * Database : testdb
        * User : dev
        * Password : 1q2w3e$R

<br />

* Profile 설정
    * 로컬환경 = spring.profiles.active = local
    * 개발환경 = spring.profiles.active = development
    * 운영환경 = spring.profiles.active = production

### 향후 추가 예상 내용? ###
* Auth(JWT: https://jwt.io/)
* Mongo DB default 설정
* Redis default 설정
* Swagger
* Google analytics: https://marketingplatform.google.com
* ELK: ElasticSearch, LogStash, Kibana
* 그 외 기타등등

### 연락처 ###

* 정성현 <hashmap27@gmail.com> or <public27@naver.com>
