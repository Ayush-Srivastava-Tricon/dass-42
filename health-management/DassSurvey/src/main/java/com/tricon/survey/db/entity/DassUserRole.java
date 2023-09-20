package com.tricon.survey.db.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "dass_user_role")
public class DassUserRole implements Serializable{
	
	private static final long serialVersionUID = -239444090513548393L;

	@EmbeddedId
	private DassUserRolePk id = new DassUserRolePk();

	@ManyToOne
	@JoinColumn(name = "uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
	private DassUser user;
	
	public void setRole(String role) {
		id.setRole(role);
	}

	public String getRole() {
		return id.getRole();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DassUserRole other = (DassUserRole) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
}
