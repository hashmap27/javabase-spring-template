package com.javabase.template.framework.web.log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javabase.template.framework.util.WebUtil;

/**
 * Logging Aspect for every invokation @RequestMapping annotation method is @Controller, @RestController annotated beans
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
@Aspect
public class ControllerLoggingAspect {
    /** Logging available to subclasses */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String LINE = "##################################################";
    private static final String LINEBREAK_TAB = "\r\n\t";

    /** type정보를 통한 조인포인트 선택 - Controller */
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() { /** AOP PointCut */ }

    /** type정보를 통한 조인포인트 선택 - RestController */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() { /** AOP PointCut */ }

    /** method패턴으로 조인포인트 선택 - 모든 method */
    @Pointcut("execution(* *(..))")
    public void methodPointcut() { /** APO PointCut */ }

    /** RequestMapping 어노테이션이 붙은 메서드를 대상으로 조인포인트 선택 */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() { /** AOP PointCut */ }

    /**
     * 로깅정책 Annotation에서 로깅 Marker 획득(없이면 null)
     */

    /**
     * JoinPoint전에 실행. 예외가 발생하는 경우만 제외하고 항상 실행
     * 컨트롤러 호출 시 동작을 의도함.
     */
    @Before("(controller() || restController()) && methodPointcut() && requestMapping()")
    public void beforControllerMethod(JoinPoint joinPoint) {
        logger.debug(LINEBREAK_TAB + LINE + "\r\n\t## START " + niceNameForStart(joinPoint));
    }

    /**
     * JoinPoint가 정상적으로 종료된 후 실행. 예외가 발생하면 실행되지 않음.
     * 컨트롤러 호출 후 동작을 의도함.
     */
    @AfterReturning(pointcut = "(controller() || restController()) && methodPointcut() && requestMapping()", returning = "returnValue")
    public void afterControllerMethod(JoinPoint joinPoint, Object returnValue) {
        logger.debug("\r\n\t## END " + niceNameForEnd(joinPoint, returnValue) + LINEBREAK_TAB + LINE);
    }

    /**
     * JoinPoint에서 예외가 발생했을 때 실행. 예외가 발생하지 않고 정상적으로 종료하면 실행되지 않음.
     * 컨트롤러 호출 중 에러에 반응하도록 동작을 의도.
     */
    @AfterThrowing(pointcut = "(controller() || restController()) && methodPointcut() && requestMapping()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.debug("\r\n\t## EXCEPTION AT " + niceNameForException(joinPoint, ex) + LINEBREAK_TAB + LINE);
    }

    /**
     * joinPoint에서 Before 로깅내용 획득
     */
    private String niceNameForStart(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName() + "#"
                + joinPoint.getSignature().getName() + "";
    }
    private String argsToString(Object[] args) {
        StringBuilder sb = new StringBuilder();
        if(args == null || args.length == 0) {
            return "\t\tno arguments.";
        }
        for(int i=0; i< args.length; i++) {
            sb.append(String.format("%n\t##  - args[%d]: %s", i, argToString(args[i])));
        }
        return sb.toString();
    }
    private String argToString(Object arg) {
        if(arg == null) {
            return "<null>";
        }
        //javax.servlet.http.HttpServletRequest
        if(arg instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) arg;
            //POST일 경우
            if(WebUtil.isFormPost(request)) {
                return arg.toString() + " - " + getRequestParameterMap(request);
            }
        }
        //NOTE: apache.catalina.connector 정보 추가 로깅 가능.
        //NOTE: 추가적으로 argumentResolver에서 획득되는 객체에 대한 로깅 가능.
        return arg.toString();
    }

    /**
     * joinPoint의 AfterReturning 로깅내용 획득
     */
    private String niceNameForEnd(JoinPoint joinPoint, Object returnValue) {
        return joinPoint.getTarget().getClass().getSimpleName()
                + "#" + joinPoint.getSignature().getName()
                + argsToString(joinPoint.getArgs())
                + LINEBREAK_TAB + "##  - return: " + ((null == returnValue) ? "" : returnValue.toString());
    }

    /**
     * joinPoint의 AfterThrowing 로깅내용 획득
     */
    private String niceNameForException(JoinPoint joinPoint, Throwable ex) {
        return joinPoint.getTarget().getClass().getSimpleName()
                + "#" + joinPoint.getSignature().getName()
                + argsToString(joinPoint.getArgs())
                + LINEBREAK_TAB + "##  - EXCEPTION : " + ex.toString();
    }

    /**
     * 요청객체의 파라미터를 로깅이 가능하도록 Map<String, String> 형태로 전환
     *
     * request.getParameterMap()은 Map<String, String[]> -> Map<String, String>으로 로깅이 가능하도록 변환.
     *
     * @param request 요청객체
     * @return Map<String, String> 형태의 요청 파라미터 맵
     */
    protected Map<String, String> getRequestParameterMap(ServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String key;
        String[] value;
        for(Entry<String, String[]> item : parameterMap.entrySet()) {
            key = item.getKey();
            value = item.getValue();
            result.put(key, Arrays.asList(value).toString());
        }
        return result;
    }
}
