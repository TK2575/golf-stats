package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class PerformanceDetail extends PerformanceSummary {

	@ToString.Exclude
	private final List<RoundDetail> roundDetails = new ArrayList<>();

	public PerformanceDetail(Collection<GolfRound> roundsUnsorted) {
		super(roundsUnsorted);
		int i = 1;
		BigDecimal resultantHandicapIndex;
		for (GolfRound each : getGolfRounds()) {
			resultantHandicapIndex = this.getHandicapRevisionHistory().getOrDefault(each.getDate(), each.getIncomingHandicapIndex());
			roundDetails.add(new RoundDetail(i, each, resultantHandicapIndex));
			i++;
		}
		Collections.reverse(this.roundDetails);
	}
}
