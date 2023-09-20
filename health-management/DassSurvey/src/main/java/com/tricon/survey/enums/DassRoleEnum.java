package com.tricon.survey.enums;

import java.util.Arrays;
import java.util.Optional;

import com.tricon.survey.util.Constants;


public enum DassRoleEnum {

	NORMAL("NORMAL","Normal User",true);

	final private String name;
	final private String fullName;
	final private boolean isRoleVisible;
	
	private DassRoleEnum(String name, String fullName, boolean isRoleVisible) {
		this.name = name;
		this.fullName = fullName;
		this.isRoleVisible = isRoleVisible;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isRoleVisible() {
		return isRoleVisible;
	}
	
	public static String validateRoles(String role) {
		Optional<DassRoleEnum> roles = Arrays.stream(values()).filter(x -> x.getName().equals(role)).findFirst();
		if (roles.isPresent()) {
			return roles.get().getName();
		}
		return null;
	}
	
	public static String generateRoleByRoleType(String roleType) {
		Optional<DassRoleEnum> roleEnum = Arrays.stream(DassRoleEnum.values()).filter(x -> x.getName().equals(roleType))
				.findFirst();
		String roleName = "", role = "";
		if (roleEnum.isPresent()) {
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(roleName);
			return role;
		}
		return null;
	}
	
	
}
