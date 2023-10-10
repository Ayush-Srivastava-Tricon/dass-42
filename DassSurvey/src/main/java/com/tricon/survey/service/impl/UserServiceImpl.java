package com.tricon.survey.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tricon.survey.customQuery.dto.DassResponseDto;
import com.tricon.survey.customQuery.dto.DassRetakeResponseDto;
import com.tricon.survey.db.entity.DassQuestion;
import com.tricon.survey.db.entity.DassQuotes;
import com.tricon.survey.db.entity.DassResponse;
import com.tricon.survey.db.entity.DassScore;
import com.tricon.survey.db.entity.DassTask;
import com.tricon.survey.db.entity.DassUser;
import com.tricon.survey.db.entity.DassUserActivity;
import com.tricon.survey.db.entity.DassUserRole;
import com.tricon.survey.db.entity.DassUserRolePk;
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
import com.tricon.survey.enums.DassCategory;
import com.tricon.survey.enums.DassRoleEnum;
import com.tricon.survey.jpa.repository.ActivityRepo;
import com.tricon.survey.jpa.repository.DassQuestionRepo;
import com.tricon.survey.jpa.repository.DassQuoteRepo;
import com.tricon.survey.jpa.repository.DassResponseRepo;
import com.tricon.survey.jpa.repository.DassScoreRepo;
import com.tricon.survey.jpa.repository.DassTaskRepo;
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
	
	@Autowired
	DassTaskRepo taskRepo;
	
	@Autowired
	DassQuoteRepo quoteRepo;
	
	@Value("${data.totalRecordperPage}")
	private int totalRecordsperPage;
	
	@Autowired
	ActivityRepo activityRepo;
	
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
		
		//if user is retake test then remove previous dass score and activities by default
		
		if(!dassUser.isFirstTimeUser() && dto.getRetakeSurvey()) {
			DassScore existingScore = dassScoreRepo.findByUserUuid(dassUser.getUuid());
			if(existingScore!=null)
			dassScoreRepo.delete(existingScore);
			
			DassUserActivity existingActivity = activityRepo.findByUserUuid(dassUser.getUuid());
			if(existingActivity!=null)
			activityRepo.delete(existingActivity);
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

	public DassRetakeTestDto checkUserAttemptTest(JwtUser jwtUser) throws Exception {
		DassRetakeTestDto dto = null;
		DassUser user = userRepo.findByEmail(jwtUser.getUsername());
		if (user != null) {
			dto = new DassRetakeTestDto();
			DassRetakeResponseDto retakeTestDto = dassResponseRepo.findRetakeTestDetailByUserUuid(user.getUuid());
			if (retakeTestDto != null) {
				dto.setIsFirstTimeUser(user.isFirstTimeUser());
				dto.setSubmittedDate(retakeTestDto.getSubmittedDate());
			} else {
				dto.setIsFirstTimeUser(user.isFirstTimeUser());
				dto.setSubmittedDate(null);
			}
		}
		return dto;
	}

	public List<QuestionPaginationDto> fetchQuestionsUsingPagination(int pageNumber)throws Exception  {
		List<QuestionPaginationDto> listDto = null;
		QuestionPaginationDto paginationDto = null;
		Pageable paging = PageRequest.of(pageNumber, totalRecordsperPage, Sort.by("Id").ascending());
		Page<DassQuestion> data = questionRepo.findAll(paging);
		if (!data.isEmpty() && data != null) {
			listDto = new ArrayList<>();
			paginationDto = new QuestionPaginationDto();
			paginationDto.setData(data.getContent());
			paginationDto.setPageNumber(data.getNumber());
			paginationDto.setTotalElements(data.getTotalElements());
			paginationDto.setHasNextElement(data.hasNext());
			paginationDto.setPageSize(data.getSize());
			listDto.add(paginationDto);
		}
		return listDto;
	}

	public List<TaskDto> fetchTasks(JwtUser jwtUser) throws Exception {

		DassUser user = userRepo.findByEmail(jwtUser.getUsername());
		DassScore existingScore = dassScoreRepo.findByUserUuid(user.getUuid());
		boolean isDepression = false;
		boolean isAnxity = false;
		boolean isStress = false;
		List<TaskDto> listOfTask = new ArrayList<>();

		if (existingScore != null) {
			int depressionScore = existingScore.getDepressionScore();
			int anxityScore = existingScore.getAnxityScore();
			int stressScore = existingScore.getStressScore();
			
			if(depressionScore==0 && anxityScore==0 && stressScore==0) {
				
				return null;
			}
			
			

			// find Maximum Score
			int maxScore = depressionScore > anxityScore
					? ((depressionScore > stressScore) ? depressionScore : stressScore)
					: ((anxityScore > stressScore) ? anxityScore : stressScore);

			if (depressionScore == maxScore) {
				isDepression = true;
			}
			if (anxityScore == maxScore) {
				isAnxity = true;
			}
			if (stressScore == maxScore) {
				isStress = true;
			}

			// check max score is the same to another score or not

			if (isDepression == true && isAnxity == true) {

				List<DassTask> depressionTask = taskRepo.findByCategory(DassCategory.DEPRESSION);
				List<DassTask> anxityTask = taskRepo.findByCategory(DassCategory.ANXIETY);

				Collections.shuffle(depressionTask);
				Collections.shuffle(anxityTask);

				depressionTask = depressionTask.subList(0, 3);
				anxityTask = anxityTask.subList(0, 2);

				logger.info("If Depression and Anxity is same:");
				logger.info("Number of Depression task:" + depressionTask.size());
				logger.info("Number of Anxity task:" + anxityTask.size());

				depressionTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});
				anxityTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});

				return listOfTask;

			}
			if (isDepression == true && isStress == true) {

				List<DassTask> depressionTask = taskRepo.findByCategory(DassCategory.DEPRESSION);
				List<DassTask> stressTask = taskRepo.findByCategory(DassCategory.STRESS);

				Collections.shuffle(depressionTask);
				Collections.shuffle(stressTask);

				depressionTask = depressionTask.subList(0, 3);
				stressTask = stressTask.subList(0, 2);

				logger.info("If Depression and Stress is same:");
				logger.info("Number of Depression task:" + depressionTask.size());
				logger.info("Number of Stress task:" + stressTask.size());

				depressionTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});
				stressTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});

				return listOfTask;

			}
			if (isAnxity == true && isStress == true) {
				List<DassTask> anxityTask = taskRepo.findByCategory(DassCategory.ANXIETY);
				List<DassTask> stressTask = taskRepo.findByCategory(DassCategory.STRESS);

				Collections.shuffle(anxityTask);
				Collections.shuffle(stressTask);

				anxityTask = anxityTask.subList(0, 3);
				stressTask = stressTask.subList(0, 2);

				logger.info("If Anxity and Stress is same:");
				logger.info("Number of Anxity task:" + anxityTask.size());
				logger.info("Number of Stress task:" + stressTask.size());

				anxityTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});
				stressTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});

				return listOfTask;
			}

			if (isDepression) {
				List<DassTask> depressionTask = taskRepo.findByCategory(DassCategory.DEPRESSION);
				Collections.shuffle(depressionTask);
				depressionTask = depressionTask.subList(0, 5);
				logger.info("If Depression is Max:");
				logger.info("Number of Depression task:" + depressionTask.size());
				depressionTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});
			} else if (isAnxity) {
				List<DassTask> anxityTask = taskRepo.findByCategory(DassCategory.ANXIETY);
				Collections.shuffle(anxityTask);
				anxityTask = anxityTask.subList(0, 5);
				logger.info("If Anxity is Max:");
				logger.info("Number of Anxity task:" + anxityTask.size());
				anxityTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});
			} else {
				List<DassTask> stressTask = taskRepo.findByCategory(DassCategory.STRESS);
				Collections.shuffle(stressTask);
				stressTask = stressTask.subList(0, 5);
				logger.info("If Stress is Max:");
				logger.info("Number of Stress task:" + stressTask.size());
				stressTask.forEach(x -> {
					TaskDto taskDto = new TaskDto();
					taskDto.setTaskName(x.getTask());
					taskDto.setTaskId(x.getId());
					listOfTask.add(taskDto);

				});
			}

		}

		return listOfTask;
	}

	public List<QuotesDto> fetchQuotes(JwtUser jwtUser) throws Exception {

		DassUser user = userRepo.findByEmail(jwtUser.getUsername());
		DassScore existingScore = dassScoreRepo.findByUserUuid(user.getUuid());
		boolean isDepression = false;
		boolean isAnxity = false;
		boolean isStress = false;
		List<QuotesDto> listOfQuotes = new ArrayList<>();

		if (existingScore != null) {

			int depressionScore = existingScore.getDepressionScore();
			int anxityScore = existingScore.getAnxityScore();
			int stressScore = existingScore.getStressScore();
			
			if (depressionScore == 0 && anxityScore == 0 && stressScore == 0) {

				return null;
			}

			// find Maximum Score
			int maxScore = depressionScore > anxityScore
					? ((depressionScore > stressScore) ? depressionScore : stressScore)
					: ((anxityScore > stressScore) ? anxityScore : stressScore);

			if (depressionScore == maxScore) {
				isDepression = true;
			}
			if (anxityScore == maxScore) {
				isAnxity = true;
			}
			if (stressScore == maxScore) {
				isStress = true;
			}

			if (isDepression) {
				List<DassQuotes> depressionQuotes = quoteRepo.findByCategory(DassCategory.DEPRESSION);
				Collections.shuffle(depressionQuotes);
				depressionQuotes = depressionQuotes.subList(0, 1);
				logger.info("If Depression is Max:");
				logger.info("Number of Depression Quotes:" + depressionQuotes.size());
				depressionQuotes.forEach(x -> {
					QuotesDto quoteDto = new QuotesDto();
					quoteDto.setQuotesName(x.getQuote());
					listOfQuotes.add(quoteDto);

				});
			} else if (isAnxity) {
				List<DassQuotes> anxityQuotes = quoteRepo.findByCategory(DassCategory.ANXIETY);
				Collections.shuffle(anxityQuotes);
				anxityQuotes = anxityQuotes.subList(0, 1);
				logger.info("If Anxity is Max:");
				logger.info("Number of Anxity Quotes:" + anxityQuotes.size());
				anxityQuotes.forEach(x -> {
					QuotesDto quoteDto = new QuotesDto();
					quoteDto.setQuotesName(x.getQuote());
					listOfQuotes.add(quoteDto);

				});
			} else {
				List<DassQuotes> stressQuotes = quoteRepo.findByCategory(DassCategory.STRESS);
				Collections.shuffle(stressQuotes);
				stressQuotes = stressQuotes.subList(0, 1);
				logger.info("If Stress is Max:");
				logger.info("Number of Stress Quotes:" + stressQuotes.size());
				stressQuotes.forEach(x -> {
					QuotesDto quoteDto = new QuotesDto();
					quoteDto.setQuotesName(x.getQuote());
					listOfQuotes.add(quoteDto);

				});
			}
		}

		return listOfQuotes;
	}

	@Transactional(rollbackOn = Exception.class)
	public ActivityResponseDto saveActivity(JwtUser jwtUser, DassActivityDto dt) throws Exception {
		DassUser user = userRepo.findByEmail(jwtUser.getUsername());
		DassUserActivity activity = null;
		ActivityResponseDto dto = null;
		if (user != null && !user.isFirstTimeUser()) {
			dto = new ActivityResponseDto();
			DassUserActivity checkExistingUser = activityRepo.findByUserUuid(user.getUuid());
			if (checkExistingUser == null) {
				activity = new DassUserActivity();
				activity.setCreatedDate(Timestamp.from(Instant.now()));
				activity.setUpdatedDate(Timestamp.from(Instant.now()));
				activity.setUser(user);
				activity.setActivity1Id(dt.getActivity1());
				activity.setActivity2Id(dt.getActivity2());
				activity.setActivity3Id(dt.getActivity3());
				activity.setActivity4Id(dt.getActivity4());
				activity.setActivity5Id(dt.getActivity5());
				activity = activityRepo.save(activity);
				dto.setCreatedDate(activity.getCreatedDate());
				dto.setUpdatedDate(activity.getUpdatedDate());
				dto.setUserUuid(activity.getUser().getUuid());
				dto.setIsActivitySubmitted(true);
				dto.setSuccessStatus(true);
			} else {
				DassUserActivity activityHistory = activityRepo.findExistingActivityByUserUuid(user.getUuid());
				if (activityHistory != null) {
					activityHistory.setUpdatedDate(Timestamp.from(Instant.now()));
					activityHistory.setActivity1Id(dt.getActivity1());
					activityHistory.setActivity2Id(dt.getActivity2());
					activityHistory.setActivity3Id(dt.getActivity3());
					activityHistory.setActivity4Id(dt.getActivity4());
					activityHistory.setActivity5Id(dt.getActivity5());
					activity = activityRepo.save(activityHistory);
					dto.setCreatedDate(activity.getCreatedDate());
					dto.setUpdatedDate(activity.getUpdatedDate());
					dto.setUserUuid(activity.getUser().getUuid());
					dto.setSuccessStatus(true);
					dto.setIsActivitySubmitted(true);
				} else {
					dto.setSuccessStatus(false);
					dto.setIsActivitySubmitted(false);
					dto.setMessage("Activites have been saved already.");
				}
			}
		}
		return dto;
	}

	public ActivityResponseDto fetchUserActivites(JwtUser jwtUser) throws Exception {
		DassUser user = userRepo.findByEmail(jwtUser.getUsername());
		ActivityResponseDto dto = null;
		if (user != null && !user.isFirstTimeUser()) {
			dto = new ActivityResponseDto();
			DassUserActivity activityHistory = activityRepo.findExistingActivityByUserUuid(user.getUuid());
			if (activityHistory != null) {
				dto.setCreatedDate(activityHistory.getCreatedDate());
				dto.setUpdatedDate(activityHistory.getUpdatedDate());
				dto.setUserUuid(activityHistory.getUser().getUuid());
				dto.setSuccessStatus(true);
				dto.setIsActivitySubmitted(true);
			} else {
				DassUserActivity checkExistingUser = activityRepo.findByUserUuid(user.getUuid());
				if (checkExistingUser == null) {
					dto.setSuccessStatus(false);
					dto.setIsActivitySubmitted(false);
					dto.setMessage("No records found");
					return dto;
				}
				List<DassTask> existingTask = taskRepo.findByIdIn(Arrays.asList(checkExistingUser.getActivity1Id(),
						checkExistingUser.getActivity2Id(), checkExistingUser.getActivity3Id(),
						checkExistingUser.getActivity4Id(), checkExistingUser.getActivity5Id()));
				if (existingTask != null && !existingTask.isEmpty()) {
					dto.setActivity1(existingTask.get(0).getTask());
					dto.setActivity2(existingTask.get(1).getTask());
					dto.setActivity3(existingTask.get(2).getTask());
					dto.setActivity4(existingTask.get(3).getTask());
					dto.setActivity5(existingTask.get(4).getTask());
					dto.setCreatedDate(checkExistingUser.getCreatedDate());
					dto.setUpdatedDate(checkExistingUser.getUpdatedDate());
					dto.setUserUuid(checkExistingUser.getUser().getUuid());
					dto.setSuccessStatus(false);
					dto.setIsActivitySubmitted(true);
					dto.setMessage("Activites have been saved already.");
				}
			}
		}
		return dto;
	}
}
