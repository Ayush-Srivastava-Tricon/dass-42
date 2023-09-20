package com.tricon.survey.security.controller;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.survey.Exception.AuthenticationException;
import com.tricon.survey.dto.GenericResponse;
import com.tricon.survey.jwt.service.JwtAuthenticationCustomResponse;
import com.tricon.survey.jwt.service.JwtAuthenticationResponse;
import com.tricon.survey.security.JwtAuthenticationRequest;
import com.tricon.survey.security.JwtTokenUtil;
import com.tricon.survey.security.JwtUser;

@CrossOrigin
@RestController
public class AuthenticationRestController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    
    @PostMapping(value = "${jwt.route.authentication.path}")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {  
		if (authenticationRequest.getUsername() == null || authenticationRequest.getUsername().trim().equals("")
				|| authenticationRequest.getPassword() == null || authenticationRequest.getPassword().trim().equals("")) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "User Logged in Failed", null));

		}
			// Reload password post-security so we can generate the token
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());  
			final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
			final String token = jwtTokenUtil.generateToken(userDetails);
			// Return the token
			return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "User Logged in Success",
					new JwtAuthenticationCustomResponse(token, userDetails.getUsername(), userDetails.getAuthorities())));

    }
    
   @GetMapping(value = "${jwt.route.authentication.refresh}")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        
        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate(),user.getActive())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            JwtAuthenticationResponse r=new JwtAuthenticationResponse(refreshedToken);
            
            return ResponseEntity.ok(new Object[] {r,user.getAuthorities()});
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
  
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }
}
