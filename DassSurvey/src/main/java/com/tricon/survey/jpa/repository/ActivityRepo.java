package com.tricon.survey.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.survey.db.entity.DassUserActivity;

public interface ActivityRepo extends JpaRepository<DassUserActivity, Integer>{

	DassUserActivity findByUserUuid(String uuid);
}
