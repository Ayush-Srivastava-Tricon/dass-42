package com.tricon.survey.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserDassResponseDto {

	private List<DassResponseDto>data;
	private Boolean retakeSurvey;
}
