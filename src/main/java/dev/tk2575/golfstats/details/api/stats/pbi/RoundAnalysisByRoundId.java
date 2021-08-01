package dev.tk2575.golfstats.details.api.stats.pbi;

import dev.tk2575.golfstats.details.api.stats.PerformanceSummaryWithRounds;
import dev.tk2575.golfstats.details.api.stats.RoundSummary;
import lombok.*;

import java.util.List;

@Getter
public class RoundAnalysisByRoundId extends RoundSummary {
	private final String golfer;

	private RoundAnalysisByRoundId(String golfer, RoundSummary summary) {
		super(summary);
		this.golfer = golfer;
	}

	static List<RoundAnalysisByRoundId> compile(PerformanceSummaryWithRounds summary) {
		return summary.getRoundSummaries().stream().map(round -> new RoundAnalysisByRoundId(summary.getGolfer(), round)).toList();
	}

}
