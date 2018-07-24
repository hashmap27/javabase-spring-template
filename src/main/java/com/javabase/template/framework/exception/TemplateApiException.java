package com.javabase.template.framework.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class TemplateApiException extends TemplateRuntimeException {
	private static final long serialVersionUID = -7699179175957213466L;

	/** Rest API 응답 코드 (Response 상태 코드) */
    private HttpStatus status;

    /** 에러 코드 */
    private String errorCode;
    /** 에러 메시지 */
    private String message;
    /** 에러 상세 메시지 */
    private String debugMessage;

	public TemplateApiException(HttpStatus status, String errorCode, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(errorCode, cause, enableSuppression, writableStackTrace);
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
		this.debugMessage = debugMessage;
	}
	public TemplateApiException(HttpStatus status, String errorCode, String message, String debugMessage, Throwable cause) {
		super(errorCode, cause);
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
		this.debugMessage = debugMessage;
	}
	public TemplateApiException(HttpStatus status, String errorCode, String message, String debugMessage) {
		super(errorCode);
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
		this.debugMessage = debugMessage;
	}
	public TemplateApiException(HttpStatus status, String errorCode, Throwable cause) {
		super(errorCode, cause);
		this.status = status;
		this.errorCode = errorCode;
		this.message = errorCode;
	}
	public TemplateApiException(HttpStatus status, String errorCode) {
		super(errorCode);
		this.status = status;
		this.errorCode = errorCode;
		this.message = errorCode;
	}

}
