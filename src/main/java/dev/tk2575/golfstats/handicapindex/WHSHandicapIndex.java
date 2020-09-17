package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.GolfRoundStream;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class WHSHandicapIndex implements HandicapIndex {

	private final String golfer;
	private final BigDecimal value;
	@ToString.Exclude private final List<GolfRound> golfRounds;

	public WHSHandicapIndex(List<GolfRound> golfRounds) {
		if (golfRounds == null || golfRounds.isEmpty()) {
			throw new IllegalArgumentException("golfRounds cannot be empty");
		}
		this.golfRounds = golfRounds;
		this.golfer = rounds().golferNames();
		this.value = rounds().compileTo18HoleRounds()
		                     .subsetMostRecentForHandicap()
		                     .getBestDifferentials()
		                     .meanDifferential();
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.golfRounds);
	}
}
