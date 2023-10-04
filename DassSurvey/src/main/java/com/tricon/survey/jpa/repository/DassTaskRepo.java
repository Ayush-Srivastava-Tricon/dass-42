package com.tricon.survey.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.survey.db.entity.DassTask;
import com.tricon.survey.enums.DassCategory;

public interface DassTaskRepo extends JpaRepository<DassTask, Integer>{

	List<DassTask>findByCategory(DassCategory cat);
}
