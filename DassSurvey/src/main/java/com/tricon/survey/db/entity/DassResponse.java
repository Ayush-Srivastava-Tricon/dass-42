package com.tricon.survey.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
@Table(name = "dass_response")
public class DassResponse {
	
	@GeneratedValue(generator = "uuid2")
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	private String uuid;
	
	@ManyToOne
	@JoinColumn(name = "dass_uuid", referencedColumnName = "uuid",updatable = false)
	private DassUser user;
	
	@ManyToOne
	@JoinColumn(name = "question_id", referencedColumnName = "id")
	private DassQuestion questionId;
	
	@Column(nullable = false, columnDefinition = "TINYINT(5)")
	private int response;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;

}
