package com.tricon.survey.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivityResponseDto {

	private String userUuid;
	private Date createdDate;
	private Date updatedDate;
	private String message;
	private boolean successStatus;
	private String activity1;
	private String activity2;
	private String activity3;
	private String activity4;
	private String activity5;
	private String message;
}
