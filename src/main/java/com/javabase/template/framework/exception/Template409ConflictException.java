package com.javabase.template.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class Template409ConflictException extends TemplateApiException {
	private static final long serialVersionUID = -3284546919898906641L;

	public Template409ConflictException(String errorCode, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(HttpStatus.CONFLICT, errorCode, message, debugMessage, cause, enableSuppression, writableStackTrace);
	}

	public Template409ConflictException(String errorCode, String message, String debugMessage, Throwable cause) {
		super(HttpStatus.CONFLICT, errorCode, message, debugMessage, cause);
	}

	public Template409ConflictException(String errorCode, String message, String debugMessage) {
		super(HttpStatus.CONFLICT, errorCode, message, debugMessage);
	}

	public Template409ConflictException(String errorCode, Throwable cause) {
		super(HttpStatus.CONFLICT, errorCode, cause);
	}

	public Template409ConflictException(String errorCode) {
		super(HttpStatus.CONFLICT, errorCode);
	}

}
