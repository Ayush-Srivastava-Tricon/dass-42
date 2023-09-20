package com.tricon.survey.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tricon.survey.db.entity.DassUser;

public interface DassUserRepo  extends JpaRepository<DassUser, String> {

	DassUser findByEmail(String email);
}
