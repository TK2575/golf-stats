package dev.tk2575.golfstats.core.handicapindex;

import dev.tk2575.golfstats.core.golfround.GolfRound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface HandicapIndex {

	//TODO opportunity to reduce duplicated code in round loops?

	Integer MINIMUM_ROUNDS_FOR_INDEX = 3;

	BigDecimal getValue();

	long getRoundCount();

	List<GolfRound> getAdjustedRounds();

	Map<LocalDate, BigDecimal> getRevisionHistory();

	static HandicapIndex newIndex(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			return emptyIndex();
		}
		return new WHSHandicapIndex(rounds);
	}

	static HandicapIndex antiHandicapOf(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			return emptyIndex();
		}
		return new AntiWHSHandicapIndex(rounds);
	}

	static HandicapIndex lastFiveRoundsTrendingHandicap(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			return emptyIndex();
		}
		return new LastNRoundsTrendingHandicap(rounds, 5);
	}

	static HandicapIndex overallHandicap(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			return emptyIndex();
		}
		return new LastNRoundsTrendingHandicap(rounds, rounds.size());
	}

	static HandicapIndex emptyIndex() {
		return new EmptyHandicapIndex();
	}

	static HandicapIndex of(BigDecimal value) { return new EmptyHandicapIndex(value); }

	static HandicapIndex medianHandicap(Collection<GolfRound> rounds) {
		if (rounds == null || rounds.isEmpty()) {
			return emptyIndex();
		}
		return new MedianHandicap(rounds);
	}
}
