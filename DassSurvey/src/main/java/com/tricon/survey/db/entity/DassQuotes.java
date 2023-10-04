package com.tricon.survey.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tricon.survey.enums.DassCategory;

import lombok.Data;

@Data
@Entity
@Table(name = "dass_quote")
public class DassQuotes implements Serializable{


	private static final long serialVersionUID = -6017880787381944432L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@Column(name = "quote",nullable = false, columnDefinition = "text")
	private String quote;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "dass_category",nullable = false)
	private DassCategory category;
	
}
