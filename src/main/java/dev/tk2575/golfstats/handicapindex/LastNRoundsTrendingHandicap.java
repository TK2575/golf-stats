package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LastNRoundsTrendingHandicap implements HandicapIndex {

	private final String golfer;
	private final BigDecimal value;
	private final List<GolfRound> rounds;

	public LastNRoundsTrendingHandicap(List<GolfRound> rounds, int maxSize) {
		validateArguments(rounds, maxSize);

		this.rounds = GolfRound.stream(rounds)
		                       .compileTo18HoleRounds()
		                       .sortNewestToOldest()
		                       .limit(maxSize)
		                       .collect(Collectors.toList());

		this.golfer = GolfRound.stream(this.rounds).golferNames();
		this.value = GolfRound.stream(this.rounds).meanDifferential();
	}

	private void validateArguments(List<GolfRound> rounds, int maxSize) {
		if (rounds == null || rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds is a required argument");
		}

		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize must be a positive integer");
		}
	}

	public long getRoundCount() {
		return rounds.size();
	}

	@Override
	public String toString() {
		return toStringDefault();
	}
}
