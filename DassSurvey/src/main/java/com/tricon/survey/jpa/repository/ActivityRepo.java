package com.tricon.survey.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.survey.db.entity.DassUserActivity;

public interface ActivityRepo extends JpaRepository<DassUserActivity, Integer>{

	DassUserActivity findByUserUuid(String uuid);
	
	@Query(nativeQuery = true, value = "select * from dass_user_activity where dass_uuid=:uuid and DATE(updated_date)<>DATE(CURDATE())")
	DassUserActivity findExistingActivityByUserUuid(@Param("uuid") String uuid);
}
