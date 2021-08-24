package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class PerformanceSummaryWithRounds extends PerformanceSummary {

	@ToString.Exclude
	private final List<RoundSummary> roundSummaries = new ArrayList<>();

	public PerformanceSummaryWithRounds(Collection<GolfRound> roundsUnsorted) {
		super(roundsUnsorted);
		int i = 1;
		BigDecimal resultantHandicapIndex;
		for (GolfRound each : getGolfRounds()) {
			resultantHandicapIndex = this.getHandicapRevisionHistory().getOrDefault(each.getDate(), each.getIncomingHandicapIndex());
			roundSummaries.add(new RoundSummary(i, each, resultantHandicapIndex));
			i++;
		}
	}
}
