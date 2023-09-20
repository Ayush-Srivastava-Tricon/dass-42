package com.tricon.survey.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import lombok.Data;

@Data
@Embeddable
public class DassUserRolePk implements Serializable{


	private static final long serialVersionUID = 1391459158270277206L;

	@Column(name = "uuid", length = 45)
	private String uuid;

	@Column(name = "role", length = 45)
	private String role;
   
	public DassUserRolePk(){}
}
