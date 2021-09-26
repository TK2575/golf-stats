package dev.tk2575.golfstats.core.handicapindex;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
class LastNRoundsTrendingHandicap implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	@JsonIgnore
	@ToString.Exclude
	private final List<GolfRound> adjustedRounds;

	private final Map<LocalDate, BigDecimal> revisionHistory = new TreeMap<>();

	LastNRoundsTrendingHandicap(@NonNull Collection<GolfRound> rounds, int maxSize) {
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds is a required argument");
		}

		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize must be a positive integer");
		}

		List<GolfRound> compiled = GolfRound.stream(rounds)
				.compileTo18HoleRounds()
				.sortNewestToOldest()
				.limit(maxSize)
				.sortOldestToNewest()
				.toList();

		BigDecimal indexCursor = BigDecimal.ZERO;
		List<GolfRound> adjusted = new ArrayList<>();
		for (GolfRound round : compiled) {
			adjusted.add(adjusted.size() < MINIMUM_ROUNDS_FOR_INDEX ? round : round.applyNetDoubleBogey(indexCursor));
			indexCursor = GolfRound.stream(adjusted).meanDifferential();
			if (adjusted.size() >= MINIMUM_ROUNDS_FOR_INDEX) {
				this.revisionHistory.put(round.getDate(), indexCursor);
			}
		}

		this.value = indexCursor;
		this.adjustedRounds = compiled;
		this.roundCount = compiled.size();
	}
}
