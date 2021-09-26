package dev.tk2575.golfstats.details.api.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@ToString
public class PerformanceSummary {

	private final String golfer;
	private final BigDecimal handicapIndex;

	@JsonIgnore @ToString.Exclude
	private final Map<LocalDate, BigDecimal> handicapRevisionHistory;

	@JsonIgnore
	@ToString.Exclude
	private final List<GolfRound> golfRounds;

	public PerformanceSummary(Collection<GolfRound> roundsUnsorted) {
		HandicapIndex index = HandicapIndex.newIndex(GolfRound.stream(roundsUnsorted).compileTo18HoleRounds().toList());
		this.golfRounds = index.getAdjustedRounds();
		this.golfer = rounds().golferNames();
		this.handicapIndex = index.getValue();
		this.handicapRevisionHistory = index.getRevisionHistory();
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.golfRounds);
	}
}
