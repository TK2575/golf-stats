package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.GolfRoundStream;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WHSHandicapIndex implements HandicapIndex {

	private final String golfer;
	private final BigDecimal value;

	@ToString.Exclude
	private final List<GolfRound> rounds;

	public WHSHandicapIndex(List<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds cannot be empty");
		}

		this.rounds = GolfRound.stream(rounds)
		                       .compileTo18HoleRounds()
		                       .sortNewestToOldest()
		                       .limit(20)
		                       .collect(Collectors.toList());

		this.golfer = rounds().golferNames();
		this.value = rounds().getBestDifferentials().meanDifferential();
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.rounds);
	}

	public long getRoundCount() {
		return this.rounds.size();
	}

	@Override
	public String toString() {
		return toStringDefault();
	}
}
