package com.javabase.template.framework.support.base;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.google.common.base.CaseFormat;
import com.javabase.template.framework.exception.TemplateRuntimeException;

/**
 * Javabase Spring Template DAO Support
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public abstract class TemplateDAOSupport extends TemplateComponentSupport {

    private static final int PAGE_SIZE_LIMIT = 1000;    //최대 페이지 크기 제한
    private static final int PAGE_SIZE_DEFAULT = 10;    //기본 페이지 크기 (파라미터 없는 경우 적용)

    protected String mapperNamespace = this.getClass().getName() + ".";

    @Value("${mybatis.configuration.map-underscore-to-camel-case}")
    private boolean mybatisMapUnderscoreToCamelCase;

    @Autowired protected SqlSession sqlSession;

    protected String prepareSortParameter(Sort sort, Class<?> voClass) {
        if(sort == null) {
            return null;
        }

        //동적 Order by 구문
        StringBuilder sb = null;

        List<String> filterPropertyNames = getPropertyNames(voClass);
        for(Order order : sort) {
            //Order Filed 구하기
            String orderProperty = convertToKnownPropertyName(order, filterPropertyNames);
            //Skip Unknown Property
            if(StringUtils.isEmpty(orderProperty)) {
                continue;
            }

            //정렬방향 구하기(기본: ASC)
            Direction direction = (order.getDirection() == null) ? Sort.DEFAULT_DIRECTION : order.getDirection();
            if(sb == null) {
                sb = new StringBuilder();   //first
            } else {
                sb.append(", ");    //second ~
            }
            sb.append(orderProperty + " " + direction);
        }

        return (sb == null) ? null : sb.toString();
    }

    /**
     * 주어진 클래스의 속성명 목록을 획득
     * @param clazz 조사할 클래스
     */
    private List<String> getPropertyNames(Class<?> clazz) {
        PropertyDescriptor[] propertyDesctiptors = PropertyUtils.getPropertyDescriptors(clazz);
        return Arrays.stream(propertyDesctiptors).map(PropertyDescriptor::getName).collect(Collectors.toList());
    }

    /**
     * 주어진 Order가 주어진 Property 목록에 있으면 반환
     * MyBatis의 mybatis.configuration.map-underscore-to-camel-case옵션에 따라 Underscore case로 변환하여 반환
     * @param order 검사할 order, order.getProperty가 필터목록에 있는지 검사
     * @param filterPropertyNames 필터링할 속성명 목록, 주로 vo/dt 클래스 속성명
     * @return order의 속성명에 주어진 property 목록에 있으면 반환, 옵션에 따라 camelCase를 UnderscoreCase로 변환하여 반환.
     */
    private String convertToKnownPropertyName(Order order, List<String> filterPropertyNames) {
        return convertToKnownPropertyName(order, filterPropertyNames, mybatisMapUnderscoreToCamelCase);
    }
    /**
     * 주어진 Order가 주어진 Property 목록에 있으면 반환 (옵션에 따라 Underscore Case로 변환하여 반환)
     * @param order 검사할 order, order.getProperty가 필터목록에 있는지 검사
     * @param filterPropertyNames 필터링할 속성명 목록,
     * @param applyCamelToUnderscore
     * @return
     */
    private String convertToKnownPropertyName(Order order, List<String> filterPropertyNames, boolean applyCamelToUnderscore) {
        String orderProperty = order.getProperty();
        //check order property filter
        for(String filterProperty : filterPropertyNames) {
            if(filterProperty.equalsIgnoreCase(orderProperty)) {
                //Exist in filter
                if(applyCamelToUnderscore) {
                    //camelCase를 undersocreCase로 변환하며 반환(myName => MY_NAME)
                    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, filterProperty);
                } else {
                    return filterProperty;
                }
            }
        }
        return null;
    }

    /**
     * 전체 건 수에 때라 {@link Pageable}의 page와 size를 올바른 값으로 보정(page >= 0, size >= 1, 최대 size제한)
     * @param pageable Pagination 정보 객체
     * @param total 전체 조회된 건수
     * @param pageSizeLimit 페이지당 최대 표시 갯수 제한
     * @return 보정된 Pageable 객체
     */
    protected Pageable ensureValidPageable(Pageable pageable, int total, int pageSizeLimit) {
        return ensureValidPageableProc(pageable, total, pageSizeLimit);
    }
    /**
     * 전체 건수에 따라 {@link Pageable}의 page와 size를 올바른값으로 보정 (page>=0, size>=1, 최대size제한)
     * @param pageable Pagination 정보 객체
     * @param total 전체 조회된 건수
     * @return 보정된 Pageable 객체
     */
    protected Pageable ensureValidPageable(Pageable pageable, int total) {
        return ensureValidPageableProc(pageable, total, PAGE_SIZE_LIMIT);
    }
    private Pageable ensureValidPageableProc(Pageable pageable, int total, int pageSizeLimit) {
        if(pageable == null) {
            return null;
        }
        if(pageable instanceof PageRequest) {
            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();
            Sort sort = pageable.getSort();
            //size
            if(size > pageSizeLimit) {
                size = pageSizeLimit;   //최대 페이지 제한
            } else if(size < 1) {
                size = PAGE_SIZE_DEFAULT;   //기본 페이지당 표시 갯수
            }
            //page
            int totalPage = (size == 0) ? 1 : (int) Math.ceil((double) total / (double) size);
            if(page <= 0) {
                page = 0;   //zero-based index
            } else if(page >= totalPage) {
                page = Math.max(totalPage - 1, 0);
            }
            return new PageRequest(page, size, sort);
        } else {
            throw new TemplateRuntimeException("UnKnown Pageable Type. type: " + pageable.getClass().getSimpleName());
        }
    }
}
