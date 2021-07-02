package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Getter
class WHSHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	WHSHandicapIndex(@NonNull Collection<GolfRound> rounds) {
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds cannot be empty");
		}

		List<GolfRound> compiled = GolfRound.stream(rounds)
		                       .compileTo18HoleRounds()
		                       .sortNewestToOldest()
		                       .limit(20)
		                       .asList();

		this.value = GolfRound.stream(compiled).getBestDifferentials().meanDifferential();
		this.roundCount = compiled.size();
	}

	@Override
	public String toString() {
		return toStringDefault();
	}
}
