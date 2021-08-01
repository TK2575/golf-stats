package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

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
		for (GolfRound each : getGolfRounds()) {
			roundSummaries.add(new RoundSummary(i, each));
			i++;
		}
	}
}
