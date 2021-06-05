package dev.tk2575.golfstats.core.handicapindex;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
class EmptyHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	EmptyHandicapIndex() {
		this.value = BigDecimal.ZERO;
		this.roundCount = 0L;
	}

	EmptyHandicapIndex(BigDecimal value) {
		this.value = value;
		this.roundCount = 20L;
	}
}
