package com.tricon.survey.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.survey.db.entity.DassQuotes;
import com.tricon.survey.enums.DassCategory;

public interface DassQuoteRepo extends JpaRepository<DassQuotes, Integer>{

	List<DassQuotes>findByCategory(DassCategory cat);
}
