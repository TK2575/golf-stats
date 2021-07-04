package dev.tk2575.golfstats.core.handicapindex;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
class WHSHandicapIndex implements HandicapIndex {

	private final BigDecimal value;
	private final long roundCount;

	@ToString.Exclude
	@JsonIgnore
	private final List<GolfRound> adjustedRounds;

	private final Map<LocalDate, BigDecimal> revisionHistory = new TreeMap<>();

	WHSHandicapIndex(@NonNull Collection<GolfRound> rounds) {
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("rounds cannot be empty");
		}

		List<GolfRound> compiled =
				GolfRound.stream(rounds)
						.compileTo18HoleRounds()
						.asList();

		BigDecimal indexCursor = BigDecimal.ZERO;
		List<GolfRound> adjusted = new ArrayList<>();
		for (GolfRound round : compiled) {
			adjusted.add(adjusted.size() < MINIMUM_ROUNDS_FOR_INDEX ? round : round.applyNetDoubleBogey(indexCursor));
			indexCursor = GolfRound.stream(adjusted).sortNewestToOldest().limit(20).lowestDifferentials().meanDifferential();
			if (adjusted.size() >= MINIMUM_ROUNDS_FOR_INDEX) {
				this.revisionHistory.put(round.getDate(), indexCursor);
			}
		}

		this.value = indexCursor;
		this.adjustedRounds = adjusted;
		this.roundCount = adjusted.size();
	}
}
