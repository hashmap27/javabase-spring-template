package com.javabase.template.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class Template400BadReqeustException extends TemplateApiException {
	private static final long serialVersionUID = 8509644351995765742L;

	public Template400BadReqeustException(String errorCode, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(HttpStatus.BAD_REQUEST, errorCode, message, debugMessage, cause, enableSuppression, writableStackTrace);
    }

    public Template400BadReqeustException(String errorCode, String message, String debugMessage, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, errorCode, message, debugMessage, cause);
    }

    public Template400BadReqeustException(String errorCode, String message, String debugMessage) {
        super(HttpStatus.BAD_REQUEST, errorCode, message, debugMessage);
    }

    public Template400BadReqeustException(String errorCode, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, errorCode, cause);
    }

    public Template400BadReqeustException(String errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode);
    }

}
