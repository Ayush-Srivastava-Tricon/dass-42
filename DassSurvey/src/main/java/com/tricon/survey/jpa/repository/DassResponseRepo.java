package com.tricon.survey.jpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tricon.survey.customQuery.dto.DassResponseDto;
import com.tricon.survey.db.entity.DassResponse;

public interface DassResponseRepo extends JpaRepository<DassResponse, String>{
	
	@Query(nativeQuery = true, value = "select dr.question_id as QuestionId,dr.response,Response "
			+ "from dass_response dr "
			+ "inner join dass_questions dq on dq.id=dr.question_id "
			+ "inner join dass_user du on du.uuid=dr.dass_uuid "
			+ "where dass_uuid=:uuid and dq.dass_category=:catg")
	List<DassResponseDto> findDassResponseByUserUuidAndCatg(@Param("uuid") String uuid, @Param("catg") int catg);
	
	@Modifying
	@Query(nativeQuery = true, value = "delete from dass_response dr "
			+ "where dr.dass_uuid=:uuid")
	int removeRetakeUserData(@Param("uuid") String uuid);

	@Query(nativeQuery = true, value = "select count(dr.response) "
			+ "from dass_response dr "
			+ "inner join dass_user du on du.uuid=dr.dass_uuid "
			+ "where dass_uuid=:uuid")
	int countDassResponseByUserUuid(@Param("uuid") String uuid);
}
