package com.tricon.survey.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
@Table(name = "dass_user")
public class DassUser extends BaseAuditEntity implements Serializable{

	private static final long serialVersionUID = 6349588377377664801L;
	
	@GeneratedValue(generator = "uuid2")
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	private String uuid;

	@Column(nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(nullable = false, length = 64)
	private String password;

	@Column(name = "first_name", nullable = false, length = 20)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 20)
	private String lastName;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private Set<DassUserRole> roles;
	
	@Column(name = "last_password_reset_date")
	private Date lastPasswordResetDate;
	
	@Column(name = "active")
	private int active;
	
	@Column(name = "first_time_user")
	private boolean isFirstTimeUser;

}
