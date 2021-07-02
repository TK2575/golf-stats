package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Getter
class AntiWHSHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	AntiWHSHandicapIndex(@NonNull Collection<GolfRound> rounds) {
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds cannot be empty");
		}

		List<GolfRound> compiledRounds = GolfRound.stream(rounds)
				.compileTo18HoleRounds()
				.sortNewestToOldest()
				.limit(20)
				.asList();

		this.roundCount = compiledRounds.size();
		this.value = GolfRound.stream(compiledRounds).getWorstDifferentials().meanDifferential();
	}

	@Override
	public String toString() { return toStringDefault(); }
}
