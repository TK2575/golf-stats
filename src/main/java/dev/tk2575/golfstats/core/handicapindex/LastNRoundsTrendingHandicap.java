package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Getter
class LastNRoundsTrendingHandicap implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	LastNRoundsTrendingHandicap(@NonNull Collection<GolfRound> rounds, int maxSize) {
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds is a required argument");
		}

		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize must be a positive integer");
		}

		List<GolfRound> compiled = GolfRound.stream(rounds)
				.compileTo18HoleRounds()
				.sortNewestToOldest()
				.limit(maxSize)
				.asList();

		this.value = GolfRound.stream(compiled).meanDifferential();
		this.roundCount = compiled.size();
	}

	@Override
	public String toString() {
		return toStringDefault();
	}
}
