package com.javabase.template.framework.mybatis.spring.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis BaseTypeHandler
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public abstract class CommonBaseTypeHandler<T> extends BaseTypeHandler<T> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
}
