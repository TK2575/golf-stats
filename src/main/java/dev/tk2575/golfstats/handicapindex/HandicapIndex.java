package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.golfround.GolfRound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface HandicapIndex {

	String getGolfer();

	BigDecimal getValue();

	long getRoundCount();

	default String toStringDefault() {
		List<String> fields = new ArrayList<>();
		fields.add(String.format("golfer=%s", getGolfer()));
		fields.add(String.format("value=%s", getValue()));
		fields.add(String.format("roundCount=%s", getRoundCount()));

		return this.getClass()
		           .getSimpleName() + "(" + String.join(", ", fields) + ")";
	}

	static HandicapIndex newIndex(List<GolfRound> rounds) {
		return new WHSHandicapIndex(rounds);
	}

	static HandicapIndex lastFiveRoundsTrendingHandicap(List<GolfRound> rounds) {
		return new LastNRoundsTrendingHandicap(rounds, 5);
	}
}
