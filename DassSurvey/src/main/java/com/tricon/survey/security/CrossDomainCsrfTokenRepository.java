package com.tricon.survey.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

@Service 
public class CrossDomainCsrfTokenRepository implements CsrfTokenRepository {
	
	public static final String XSRF_HEADER_NAME = "X-XSRF-TOKEN";
    public static final String XSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";

    private final CookieCsrfTokenRepository delegate = new CookieCsrfTokenRepository();

    public CrossDomainCsrfTokenRepository() {
        delegate.setCookieHttpOnly(true);
        delegate.setCookieName(XSRF_TOKEN_COOKIE_NAME);
    }

	@Override
	public CsrfToken generateToken(HttpServletRequest request) {
		  return delegate.generateToken(request);
	}

	@Override
	public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
		 delegate.saveToken(token, request, response);
		
	}

	@Override
	public CsrfToken loadToken(HttpServletRequest request) {
		  return delegate.loadToken(request);
	}

}
