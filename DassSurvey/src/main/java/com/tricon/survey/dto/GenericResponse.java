package com.tricon.survey.dto;

import org.springframework.http.HttpStatus;

public class GenericResponse {

	String message;
	Object data;
	HttpStatus status;
	
	public GenericResponse(HttpStatus status,String message, Object data) {
		this.status = status;
		this.message = message;
		this.data = data;
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
	
}
