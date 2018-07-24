package com.javabase.template.framework.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javabase.template.framework.exception.TemplateRuntimeException;

/**
 * BigDecimal 헬퍼
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class BigDecimalUtil {
    private static final Logger logger = LoggerFactory.getLogger(BigDecimalUtil.class);

    private BigDecimalUtil() { /* do nothing */ }

    /**
     * 원본 BigDecimal값을 동등한 float값으로 변경(유효숫자등으로 값이 달라지는 경우 Exception throw)
     * @param origin 원본 BigDecimal
     * @return float 값
     */
    public static float toEquivalentFloat(BigDecimal origin) {
        float floatValue = origin.floatValue();
        BigDecimal recovered = new BigDecimal(floatValue);  //valueOf 로 하면 BigDecimal도 2.14E9와 같이 지수형으로 처리됨. new 로 생성 처리함.
        if(origin.compareTo(recovered) != 0) {
            logger.debug("FAILED toEquivalentFloat. origin: {} => floatValue: {} => d: {}", origin, floatValue, recovered);
            throw new TemplateRuntimeException("cannot.convert.bigdecimal.to.float");
        }
        return floatValue;
    }

    /**
     * 원본 Float값을 동등한 BigDecimal값으로 변경. (지수형이 아닌 PlainString형으로 획득)
     * @param value 원본 float 값
     * @return BigDecimal 값
     */
    public static BigDecimal formFloat(float value) {
        BigDecimal bigDecimalValue = BigDecimal.valueOf(value);
        bigDecimalValue = new BigDecimal(bigDecimalValue.toPlainString());
        return bigDecimalValue;
    }

}
