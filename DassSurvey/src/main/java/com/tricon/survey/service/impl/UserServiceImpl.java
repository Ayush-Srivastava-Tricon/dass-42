package com.tricon.survey.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tricon.survey.customQuery.dto.DassResponseDto;
import com.tricon.survey.db.entity.DassQuestion;
import com.tricon.survey.db.entity.DassResponse;
import com.tricon.survey.db.entity.DassScore;
import com.tricon.survey.db.entity.DassUser;
import com.tricon.survey.db.entity.DassUserRole;
import com.tricon.survey.db.entity.DassUserRolePk;
import com.tricon.survey.dto.DassInterpritingDto;
import com.tricon.survey.dto.GenericResponse;
import com.tricon.survey.dto.UserDassResponseDto;
import com.tricon.survey.dto.UserRegistrationDto;
import com.tricon.survey.enums.DassCategory;
import com.tricon.survey.enums.DassRoleEnum;
import com.tricon.survey.jpa.repository.DassQuestionRepo;
import com.tricon.survey.jpa.repository.DassResponseRepo;
import com.tricon.survey.jpa.repository.DassScoreRepo;
import com.tricon.survey.jpa.repository.DassUserRepo;
import com.tricon.survey.jpa.repository.DassUserRoleRepo;
import com.tricon.survey.security.JwtUser;
import com.tricon.survey.util.Constants;
import com.tricon.survey.util.EncrytedKeyUtil;
import com.tricon.survey.util.MessageConstants;

@Service
public class UserServiceImpl {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DassUserRepo userRepo;
	
	@Autowired
	DassUserRoleRepo userRoleRepo;
	
	@Autowired
	DassQuestionRepo questionRepo;
	
	@Autowired
	DassResponseRepo dassResponseRepo;
	
	@Autowired
	DassScoreRepo dassScoreRepo;
	
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse registerUser(UserRegistrationDto dto) throws Exception {
		DassUser user = null;
		DassUserRole role = null;
		DassUserRolePk pk = null;
		user = userRepo.findByEmail(dto.getEmail());
		if (user == null) {
			user = convertDtotoModel(dto);
			user = userRepo.save(user);
			if (user != null) {
				// save role
				role = new DassUserRole();
				pk = new DassUserRolePk();
				pk.setUuid(user.getUuid());
				role.setId(pk);
				role.setRole(DassRoleEnum.generateRoleByRoleType(Constants.NORMAL));
				userRoleRepo.save(role);
			}

			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_CREATION, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_EXIST_WITH_EMAIL, null);
	}
	
	private DassUser convertDtotoModel(UserRegistrationDto dto) {
		DassUser user = new DassUser();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setPassword(EncrytedKeyUtil.encryptKey(dto.getPassword()));
		user.setActive(1);
		user.setFirstTimeUser(true);
		user.setEmail(dto.getEmail());
		user.setCreatedBy(user);
		return user;
	}

	public List<DassQuestion> retriveQuestions() throws Exception {
		return questionRepo.findAll();
	}

	@Transactional(rollbackOn = Exception.class)
	public Boolean saveDassResponse(UserDassResponseDto dto, JwtUser jwtUser) throws Exception {

		// validate questions size is 42 or not
		if (dto.getData().size() != 42) {
			logger.error("Quesion is missing");
			return null;
		}

		DassUser dassUser = userRepo.findByEmail(jwtUser.getUsername());
		// check response is Already submitted or not for every request

		if (!dassUser.isFirstTimeUser() && !dto.getRetakeSurvey()) {
			logger.error("User Alreday submitted response");
			return null;
		}

		List<Integer> questionIds = dto.getData().stream().map(x -> x.getQuestionId()).collect(Collectors.toList());
		// validates question Id from db

		List<Integer> validateQuestions = this.validateQuestions(questionIds);
		if (!validateQuestions.isEmpty() || validateQuestions == null) {
			logger.error("Question is not validate");
			return null;
		}

		// if user is retake then we edit exist user response

		int existingDassScoreUser = dassResponseRepo.countDassResponseByUserUuid(dassUser.getUuid());

		if (existingDassScoreUser > 0) {
			dassResponseRepo.removeRetakeUserData(dassUser.getUuid());
		}

		dto.getData().forEach(x -> {
			DassResponse response = new DassResponse();
			DassQuestion question = new DassQuestion();
			question.setId(x.getQuestionId());
			response.setQuestionId(question);
			response.setResponse(x.getResponseStatus());
			response.setUser(dassUser);
			response.setCreatedDate(Timestamp.from(Instant.now()));
			dassResponseRepo.save(response);
			dassUser.setFirstTimeUser(false);
			userRepo.save(dassUser);
		});
		return true;
	}

	private List<Integer> validateQuestions(List<Integer> questions) {
		List<Integer> incorrectQuestionList = new ArrayList<>();
		if (!questions.isEmpty()) {
			for (int id : questions) {
				Optional<DassQuestion> validateId = questionRepo.findById(id);
				if (!validateId.isPresent()) {
					incorrectQuestionList.add(id);
				}
			}
			return incorrectQuestionList;
		}
		return null;
	}

	@Transactional(rollbackOn = Exception.class)
	public DassInterpritingDto calculateDassScore(JwtUser jwtUser) throws Exception {

		DassUser dassUser = userRepo.findByEmail(jwtUser.getUsername());

		int depressionScore = 0;
		int anxityScore = 0;
		int stressScore = 0;

		DassInterpritingDto responseDto = null;

		for (DassCategory catg : DassCategory.values()) {

			List<DassResponseDto> data = dassResponseRepo.findDassResponseByUserUuidAndCatg(dassUser.getUuid(),
					catg.ordinal());
			if (!data.isEmpty() && catg.ordinal() == 0) {
				depressionScore = data.stream().map(x -> x.getResponse()).reduce(0, Integer::sum);
			}
			if (!data.isEmpty() && catg.ordinal() == 1) {
				anxityScore = data.stream().map(x -> x.getResponse()).reduce(0, Integer::sum);
			}
			if (!data.isEmpty() && catg.ordinal() == 2) {
				stressScore = data.stream().map(x -> x.getResponse()).reduce(0, Integer::sum);
			}
		}
		responseDto = new DassInterpritingDto();
		responseDto.setDepressionScore(depressionScore);
		responseDto.setAnxityScore(anxityScore);
		responseDto.setStressScore(stressScore);
		responseDto.setDassUserUuid(dassUser.getUuid());
		DassInterpritingDto.DassFinalScore finalDassScore = new DassInterpritingDto().new DassFinalScore();
		finalDassScore.setFinalScore(depressionScore + anxityScore + stressScore);
		responseDto.setFinalDassScore(finalDassScore);

		// save Dass score

		DassScore existingScore = dassScoreRepo.findByUserUuid(dassUser.getUuid());
		if (existingScore == null) {
			DassScore dassScore = new DassScore();
			dassScore.setDepressionScore(depressionScore);
			dassScore.setAnxityScore(anxityScore);
			dassScore.setStressScore(stressScore);
			dassScore.setUser(dassUser);
			dassScoreRepo.save(dassScore);
		} else {

			// edit existing score
			existingScore.setDepressionScore(depressionScore);
			existingScore.setAnxityScore(anxityScore);
			existingScore.setStressScore(stressScore);
			dassScoreRepo.save(existingScore);
		}
		return responseDto;
	}

	public Boolean checkUserAttemptTest(JwtUser jwtUser) {
		DassUser user = userRepo.findByEmail(jwtUser.getUsername());
		if (user != null) {
			return user.isFirstTimeUser();
		}
		return null;
	}
}
