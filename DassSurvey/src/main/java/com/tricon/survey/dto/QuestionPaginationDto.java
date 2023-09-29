package com.tricon.survey.dto;

import java.util.List;

import com.tricon.survey.db.entity.DassQuestion;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
public class QuestionPaginationDto {

	    private List<DassQuestion>data;
		private Integer pageSize;
		private Integer pageNumber;
		private Long totalElements;
		private boolean hasNextElement;
}
