package com.tricon.survey.service.impl;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.tricon.survey.db.entity.DassUser;
import com.tricon.survey.db.entity.DassUserRole;
import com.tricon.survey.db.entity.DassUserRolePk;
import com.tricon.survey.dto.GenericResponse;
import com.tricon.survey.dto.UserRegistrationDto;
import com.tricon.survey.enums.DassRoleEnum;
import com.tricon.survey.jpa.repository.DassUserRepo;
import com.tricon.survey.jpa.repository.DassUserRoleRepo;
import com.tricon.survey.util.Constants;
import com.tricon.survey.util.EncrytedKeyUtil;
import com.tricon.survey.util.MessageConstants;

@Service
public class UserServiceImpl {

	@Autowired
	DassUserRepo userRepo;
	
	@Autowired
	DassUserRoleRepo userRoleRepo;
	
	@Transactional(rollbackOn = Exception.class)
	public GenericResponse registerUser(UserRegistrationDto dto) throws Exception {
		DassUser user = null;
		DassUserRole role = null;
		DassUserRolePk pk = null;
		user = userRepo.findByEmail(dto.getEmail());
		if (user == null) {
			user = convertDtotoModel(dto);
			user = userRepo.save(user);
			if (user != null) {
				// save role
				role = new DassUserRole();
				pk = new DassUserRolePk();
				pk.setUuid(user.getUuid());
				role.setId(pk);
				role.setRole(DassRoleEnum.generateRoleByRoleType(Constants.NORMAL));
				userRoleRepo.save(role);
			}

			return new GenericResponse(HttpStatus.OK, MessageConstants.USER_CREATION, null);
		}
		return new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.USER_EXIST_WITH_EMAIL, null);
	}
	
	private DassUser convertDtotoModel(UserRegistrationDto dto) {
		DassUser user = new DassUser();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setPassword(EncrytedKeyUtil.encryptKey(dto.getPassword()));
		user.setActive(1);
		user.setEmail(dto.getEmail());
		user.setCreatedBy(user);
		return user;
	}

}
