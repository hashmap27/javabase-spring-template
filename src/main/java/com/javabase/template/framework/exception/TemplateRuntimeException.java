package com.javabase.template.framework.exception;

/**
 * JavaBase Template의 RuntimeException
 * 기본 RuntimeException 대신 사용
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class TemplateRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -2594736365361526710L;

    public TemplateRuntimeException() {
        super();
    }

    public TemplateRuntimeException(String message) {
        super(message);
    }

    public TemplateRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateRuntimeException(Throwable cause) {
        super(cause);
    }

    public TemplateRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
