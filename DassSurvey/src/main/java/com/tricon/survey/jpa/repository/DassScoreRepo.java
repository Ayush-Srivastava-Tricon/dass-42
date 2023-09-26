package com.tricon.survey.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.tricon.survey.db.entity.DassScore;

public interface DassScoreRepo  extends JpaRepository<DassScore, Integer>{


	DassScore findByUserUuid(@Param("uuid") String uuid);
}
