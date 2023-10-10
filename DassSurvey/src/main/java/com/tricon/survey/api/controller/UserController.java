package com.tricon.survey.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.survey.db.entity.DassQuestion;
import com.tricon.survey.dto.ActivityResponseDto;
import com.tricon.survey.dto.DassActivityDto;
import com.tricon.survey.dto.DassInterpritingDto;
import com.tricon.survey.dto.DassRetakeTestDto;
import com.tricon.survey.dto.GenericResponse;
import com.tricon.survey.dto.QuestionPaginationDto;
import com.tricon.survey.dto.QuotesDto;
import com.tricon.survey.dto.TaskDto;
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
	@PreAuthorize("hasRole('NORMAL')")
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
	@PreAuthorize("hasRole('NORMAL')")
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
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> fetchDassScore() {
		DassInterpritingDto response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.calculateDassScore(jwtUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "/checkUserAttemptTest")
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> isFirstTimeUser() {
		DassRetakeTestDto response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.checkUserAttemptTest(jwtUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "/dass-questions/{pageNumber}")
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> dassQuestions(@PathVariable("pageNumber") int pageNumber) {
		List<QuestionPaginationDto> response = null;
		try {
			response = userService.fetchQuestionsUsingPagination(pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "/fetch-task")
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> fetchTask() {
		List<TaskDto> response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.fetchTasks(jwtUser);
			if(response==null) {
				return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "You are not allowed to visit this page", response));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "/fetch-quotes")
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> fetchQuotes() {
		List<QuotesDto> response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.fetchQuotes(jwtUser);
			if(response==null) {
				return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "You are not allowed to visit this page", response));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@PostMapping(value = "/save-activity")
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> saveActivites(@RequestBody DassActivityDto dto) {
		ActivityResponseDto response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		if (dto.getActivity1() == null || dto.getActivity2() == null || dto.getActivity3() == null || dto.getActivity4()==null
				|| dto.getActivity5()==null) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
			
		try {
			response = userService.saveActivity(jwtUser,dto);
			if(response==null) {
				return ResponseEntity
						.ok(new GenericResponse(HttpStatus.BAD_REQUEST, "Something went Wrong", null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
	
	@GetMapping(value = "/fetch-user-activity")
	@PreAuthorize("hasRole('NORMAL')")
	public ResponseEntity<?> fetchUserActivites() {
		ActivityResponseDto response = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails) principal).getUsername());
		JwtUser jwtUser = (JwtUser) userDetails;
		try {
			response = userService.fetchUserActivites(jwtUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", response));
	}
}
