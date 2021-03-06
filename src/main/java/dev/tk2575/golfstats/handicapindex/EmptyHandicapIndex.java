package dev.tk2575.golfstats.handicapindex;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
public class EmptyHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	public EmptyHandicapIndex() {
		this.value = BigDecimal.ZERO;
		this.roundCount = 0L;
	}

	public EmptyHandicapIndex(BigDecimal value) {
		this.value = value;
		this.roundCount = 20L;
	}
}
