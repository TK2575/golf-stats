package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class PerformanceDetail extends PerformanceSummary {

	@ToString.Exclude
	private final List<RoundDetail> roundDetails = new ArrayList<>();

	public PerformanceDetail(Collection<GolfRound> roundsUnsorted) {
		super(roundsUnsorted);
		int i = 1;
		for (GolfRound each : getGolfRounds()) {
			roundDetails.add(new RoundDetail(i, each));
			i++;
		}
	}
}
