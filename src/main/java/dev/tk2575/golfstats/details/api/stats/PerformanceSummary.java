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

	@ToString.Exclude
	private final List<BigDecimal> recentDifferentials;

	@ToString.Exclude
	private final Map<LocalDate, BigDecimal> handicapRevisionHistory;

	@JsonIgnore
	@ToString.Exclude
	private final List<GolfRound> golfRounds;

	public PerformanceSummary(Collection<GolfRound> roundsUnsorted) {
		HandicapIndex index = HandicapIndex.newIndex(roundsUnsorted);
		this.golfRounds = index.getAdjustedRounds();
		this.golfer = rounds().golferNames();
		this.handicapIndex = index.getValue();
		this.handicapRevisionHistory = index.getRevisionHistory();
		this.recentDifferentials =
				this.golfRounds.stream()
						.sorted(Comparator.comparing(GolfRound::getDate).reversed())
						.limit(20)
						.map(GolfRound::getScoreDifferential)
						.toList();
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.golfRounds);
	}
}
