package com.tricon.survey.api.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.tricon.survey.dto.GenericResponse;
import com.tricon.survey.dto.UserRegistrationDto;
import com.tricon.survey.service.impl.UserServiceImpl;
import com.tricon.survey.util.MessageConstants;

@RestController
@CrossOrigin
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	UserServiceImpl userService;

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
}
