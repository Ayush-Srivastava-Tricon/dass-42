package com.tricon.survey.db.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditEntity {

	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

    @UpdateTimestamp
	@Column(name = "updated_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by",referencedColumnName="uuid")
	private DassUser createdBy;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by",referencedColumnName="uuid")
	private DassUser updatedBy;
}
