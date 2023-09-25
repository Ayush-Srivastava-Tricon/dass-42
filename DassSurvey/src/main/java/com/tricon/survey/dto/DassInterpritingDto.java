package com.tricon.survey.dto;

import lombok.Data;

@Data
public class DassInterpritingDto {

	private Integer depressionScore;
	private Integer anxityScore;
	private Integer stressScore;
	private String dassUserUuid;
	private DassFinalScore finalDassScore;

	public class DassFinalScore {

		private Integer finalScore;

		public Integer getFinalScore() {
			return finalScore;
		}

		public void setFinalScore(Integer finalScore) {
			this.finalScore = finalScore;
		}

	}

}
