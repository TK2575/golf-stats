package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
class LastNRoundsTrendingHandicap implements HandicapIndex {

	private final String golfer;
	private final BigDecimal value;
	private final Collection<GolfRound> rounds;

	LastNRoundsTrendingHandicap(Collection<GolfRound> rounds, int maxSize) {
		validateArguments(rounds, maxSize);

		this.rounds = GolfRound.stream(rounds).compileTo18HoleRounds().sortNewestToOldest().limit(maxSize).asList();

		this.golfer = GolfRound.stream(this.rounds).golferNames();
		this.value = GolfRound.stream(this.rounds).meanDifferential();
	}

	private void validateArguments(Collection<GolfRound> rounds, int maxSize) {
		if (rounds == null || rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds is a required argument");
		}

		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize must be a positive integer");
		}
	}

	@Override
	public long getRoundCount() {
		return rounds.size();
	}

	@Override
	public String toString() {
		return toStringDefault();
	}
}
