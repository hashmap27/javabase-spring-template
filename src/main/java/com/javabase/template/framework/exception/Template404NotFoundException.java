package com.javabase.template.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class Template404NotFoundException extends TemplateApiException {
    private static final long serialVersionUID = -6188871656643410832L;

    public Template404NotFoundException(String errorCode, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(HttpStatus.NOT_FOUND, errorCode, message, debugMessage, cause, enableSuppression, writableStackTrace);
    }

    public Template404NotFoundException(String errorCode, String message, String debugMessage, Throwable cause) {
        super(HttpStatus.NOT_FOUND, errorCode, message, debugMessage, cause);
    }

    public Template404NotFoundException(String errorCode, String message, String debugMessage) {
        super(HttpStatus.NOT_FOUND, errorCode, message, debugMessage);
    }

    public Template404NotFoundException(String errorCode, Throwable cause) {
        super(HttpStatus.NOT_FOUND, errorCode, cause);
    }

    public Template404NotFoundException(String errorCode) {
        super(HttpStatus.NOT_FOUND, errorCode);
    }

}
