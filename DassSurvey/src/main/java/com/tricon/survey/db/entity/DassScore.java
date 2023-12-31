package com.tricon.survey.db.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "dass_score")
public class DassScore implements Serializable{

	private static final long serialVersionUID = -2370122000620564790L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "dass_uuid", referencedColumnName = "uuid",updatable = false)
	private DassUser user;
	
	@Column(name = "depression_score", nullable = false)
	private Integer depressionScore;
	
	@Column(name = "anxity_score", nullable = false)
	private Integer anxityScore;
	
	@Column(name = "stress_score", nullable = false)
	private Integer stressScore;
}
