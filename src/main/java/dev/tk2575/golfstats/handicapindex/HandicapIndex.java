package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.golfround.GolfRound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface HandicapIndex {

	BigDecimal getValue();

	long getRoundCount();

	default String toStringDefault() {
		List<String> fields = new ArrayList<>();
		fields.add(String.format("value=%s", getValue()));
		fields.add(String.format("roundCount=%s", getRoundCount()));

		return this.getClass()
		           .getSimpleName() + "(" + String.join(", ", fields) + ")";
	}

	static HandicapIndex newIndex(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			return emptyIndex();
		}

		return new WHSHandicapIndex(rounds);
	}

	static HandicapIndex antiHandicapOf(Collection<GolfRound> rounds) { return new AntiWHSHandicapIndex(rounds); }

	static HandicapIndex lastFiveRoundsTrendingHandicap(Collection<GolfRound> rounds) {
		return new LastNRoundsTrendingHandicap(rounds, 5);
	}

	static HandicapIndex overallHandicap(Collection<GolfRound> rounds) {
		return new LastNRoundsTrendingHandicap(rounds, rounds.size());
	}

	static HandicapIndex emptyIndex() {
		return new EmptyHandicapIndex();
	}

	static HandicapIndex of(BigDecimal value) { return new EmptyHandicapIndex(value); }

	static HandicapIndex medianHandicap(Collection<GolfRound> rounds) {
		return new MedianHandicap(rounds);
	}
}
