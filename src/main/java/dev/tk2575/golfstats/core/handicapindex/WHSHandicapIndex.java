package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
class WHSHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final Collection<GolfRound> rounds;

	WHSHandicapIndex(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds cannot be empty");
		}

		this.rounds = GolfRound.stream(rounds)
		                       .compileTo18HoleRounds()
		                       .sortNewestToOldest()
		                       .limit(20)
		                       .asList();

		this.value = rounds().getBestDifferentials().meanDifferential();
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.rounds);
	}

	@Override
	public long getRoundCount() {
		return this.rounds.size();
	}

	@Override
	public String toString() {
		return toStringDefault();
	}
}
