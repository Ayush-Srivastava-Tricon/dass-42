package com.tricon.survey.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.survey.db.entity.DassQuestion;
import com.tricon.survey.dto.DassInterpritingDto;
import com.tricon.survey.dto.GenericResponse;
import com.tricon.survey.dto.UserDassResponseDto;
import com.tricon.survey.dto.UserRegistrationDto;
import com.tricon.survey.security.JwtUser;
import com.tricon.survey.service.impl.UserServiceImpl;
import com.tricon.survey.util.MessageConstants;

@RestController
@CrossOrigin
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	UserServiceImpl userService;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

	@PostMapping(value = "/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
		GenericResponse response = null;
		if (dto.getEmail() == null || dto.getFirstName() == null || dto.getLastName() == null
				|| dto.getPassword() == null) {
			return ResponseEntity.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			response = userService.registerUser(dto);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value = "/fetch-questions")
	public ResponseEntity<?> getQuestions() {
		List<DassQuestion> response = null;
		try {
			response = userService.retriveQuestions();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping(value = "/save_dass_response")
	public ResponseEntity<?> saveUserDassResponse(@RequestBody UserDassResponseDto dto) {
		Boolean response = null;

		if (dto.getData() == null || dto.getData().isEmpty() ||dto.getRetakeSurvey()==null
				|| dto.getData().stream().anyMatch(x -> (x.getQuestionId() == null || x.getResponseStatus() == null)
						|| !(x.getResponseStatus() >= 0 && x.getResponseStatus() <= 3))) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.saveDassResponse(dto, jwtUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "/fetch_dass_score")
	public ResponseEntity<?> getDassScore() {
		DassInterpritingDto response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.fetchDassScore(jwtUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
}
