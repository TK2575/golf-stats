package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@ToString
class EmptyHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	@ToString.Exclude
	private final List<GolfRound> adjustedRounds = new ArrayList<>();

	EmptyHandicapIndex() {
		this.value = BigDecimal.ZERO;
		this.roundCount = 0L;
	}

	EmptyHandicapIndex(BigDecimal value) {
		this.value = value;
		this.roundCount = 20L;
	}

	@Override
	public Map<LocalDate, BigDecimal> getRevisionHistory() {
		return new TreeMap<>();
	}
}
