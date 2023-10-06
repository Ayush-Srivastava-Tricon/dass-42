package com.tricon.survey.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;


@Data
@Entity
@Table(name = "dass_user_activity")
public class DassUserActivity implements Serializable{
	
	private static final long serialVersionUID = -5641652531418675399L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "dass_uuid", referencedColumnName = "uuid",updatable = false)
	private DassUser user;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date")
	private Date updatedDate;
}
