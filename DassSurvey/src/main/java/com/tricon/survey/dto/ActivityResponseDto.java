package com.tricon.survey.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ActivityResponseDto {

	private String userUuid;
	private Date createdDate;
	private Date updatedDate;
}
