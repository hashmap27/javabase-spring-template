package com.javabase.template.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.util.StringUtils;

import com.javabase.template.framework.exception.TemplateRuntimeException;

/**
 * Joda DateTime, Java Date 관련 유틸
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class DateUtil {

    private DateUtil() { /**/ }

    /**
     * 문자열을 Joda DateTime으로 변환
     * @param dateString yyyy/MM/dd, yyyy-MM-dd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSSz
     * @return Joda DateTime
     * @throws FmsInvalidFormatException 요청 된 DateTime 문자열의 형식이 올바르지 않은 경우 발생
     */
    public static DateTime parseToDateTime(String dateString) {
        return parseToDateTime(dateString, true);
    }

    /**
     * 문자열을 Joda DateTime으로 변환
     * @param dateString yyyy/MM/dd, yyyy-MM-dd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSSz
     * @param throwException 처리 중 예외가 발생하면 throw 할 것인지 여부
     * @return Joda DateTime (throwException이 false면 변환 실패 시 null 반환)
     * @throws FmsInvalidFormatException 요청 된 DateTime 문자열의 형식이 올바르지 않은 경우 발생
     */
    public static DateTime parseToDateTime(String dateString, Boolean throwException) {
        if(StringUtils.isEmpty(dateString)) {
            return null;
        }

        DateTimeFormatter dateFormatter = null;

        if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        } else if(dateString.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        } else if(dateString.matches("^\\d{8}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        } else if (dateString.matches("^\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        } else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        } else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$") ||
                dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}$") ||
                dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\x2E\\d{3}[+-]{1}\\d{2}:\\d{2}$")) {
            dateFormatter = ISODateTimeFormat.dateTime();
        } else {
            if(throwException) {
                throw new TemplateRuntimeException("Invalid DateTime Format. ['" + dateString + "']");
            } else {
                return null;
            }
        }

        return dateFormatter.parseDateTime(dateString);
    }

    /**
     * 문자열을 Joda LocalDateTime으로 변환
     * @param dateString yyyy/MM/dd, yyyy-MM-dd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSSz
     * @return Joda LocalDateTime
     * @throws FmsInvalidFormatException 요청 된 LocalDateTime 문자열의 형식이 올바르지 않은 경우 발생
     */
    public static LocalDateTime parseToLocalDateTime(String dateString) {
        return parseToLocalDateTime(dateString, true);
    }

    /**
     * 문자열을 Joda LocalDateTime으로 변환
     * @param dateString yyyy/MM/dd, yyyy-MM-dd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSSz
     * @param throwException 처리 중 예외가 발생하면 throw 할 것인지 여부
     * @return Joda LocalDateTime (throwException이 false면 변환 실패 시 null 반환)
     * @throws FmsInvalidFormatException 요청 된 LocalDateTime 문자열의 형식이 올바르지 않은 경우 발생
     */
    public static LocalDateTime parseToLocalDateTime(String dateString, Boolean throwException) {
        if(StringUtils.isEmpty(dateString)) {
            return null;
        }

        DateTimeFormatter dateFormatter = null;

        if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        } else if (dateString.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        } else if (dateString.matches("^\\d{8}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        } else if (dateString.matches("^\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        } else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
            dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        } else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$") ||
                dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}$") ||
                dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\x2E\\d{3}[+-]{1}\\d{2}:\\d{2}$")) {
            dateFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
        } else {
            if(throwException) {
                throw new TemplateRuntimeException("Invalid DateTime Format. ['" + dateString + "']");
            } else {
                return null;
            }
        }

        return dateFormatter.parseLocalDateTime(dateString);
    }

    /**
     * Date객체를 주어진 포맷 문자열로 획득
     * @param date  Date 객체
     * @param formatString  포맷 문자열
     * @return 주어진 Date 객체를 주어진 포맷 문자열로 획득
     */
    public static String formatDate(Date date, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        return formatter.format(date);
    }

    /** yyyMMdd로 포맷팅 */
    public static String yyyyMMdd(Date date) { return formatDate(date, "yyyyMMdd"); }
    /** yyyyMMddHHmmss로 포맷팅 */
    public static String yyyyMMddHHmmss(Date date) { return formatDate(date, "yyyyMMddHHmmss"); }
    /** yyyyMMddHHmmssSSSS로 포맷팅 */
    public static String yyyyMMddHHmmssSSSS(Date date) { return formatDate(date, "yyyyMMddHHmmssSSSS"); }

    /** 현재 시간 추출 */
    public static DateTime getNowDate() { return new DateTime(); }
    /** 현재 시간 추출(LocalDate 기준) */
    public static LocalDateTime getNowLocalDate() { return new LocalDateTime(); }

    /**
     * 현재 시간을 주어진 포맷 문자열로 획득
     * @param formatString 포맷 문자열
     * @return 현재 시간을 주어진 포맷 문자열로 획득
     */
    public static String formatNowDate(String formatString) {
        return formatDate(new Date(), formatString);
    }

    public static String yyyy() { return formatNowDate("yyyy"); }
    public static String MM() { return formatNowDate("MM"); }
    public static String dd() { return formatNowDate("dd"); }
    public static String HH() { return formatNowDate("HH"); }
    public static String HHmmss() { return formatNowDate("HHmmss"); }
    public static String yyyyMM() { return formatNowDate("yyyyMM"); }
    public static String yyyyMMdd() { return formatNowDate("yyyyMMdd"); }
    public static String yyyyMMddHHmmss() { return formatNowDate("yyyyMMddHHmmss"); }
    public static String yyyyMMddHHmmssSSSS() { return formatNowDate("yyyyMMddHHmmssSSSS"); }

    /**
     * 현재 Date의 주어진 날짜만큼 이전/다음 날을 획득
     * @param dayValue 원하는 만큼의 이전/다음 날의 숫자
     */
    private static Date getDateAfterDays(int dayValue) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, dayValue);
        return cal.getTime();
    }

    /** 현재 Date의 주어진 날짜 만큼 이전/다음 날의 yyyyMMdd 획득 */
    public static String yyyyMMddAfterDays(int dayValue) { return yyyyMMdd(getDateAfterDays(dayValue)); }

}
