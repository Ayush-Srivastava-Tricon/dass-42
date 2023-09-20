package com.tricon.survey.jwt.service;

import java.io.Serializable;

public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 6089535428477629963L;
		private final String token;

	    public JwtAuthenticationResponse(String token) {
	        this.token = token;
	    }

	    public String getToken() {
	        return this.token;
	    }
}
