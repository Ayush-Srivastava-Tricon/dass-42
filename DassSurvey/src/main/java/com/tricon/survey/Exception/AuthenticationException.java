package com.tricon.survey.Exception;

public class AuthenticationException  extends RuntimeException {

	private static final long serialVersionUID = -6161145751076268531L;

	public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
