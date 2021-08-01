package dev.tk2575.golfstats.details.api.analysis;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RoundAnalysisByRoundId extends RoundSummary {
	private final String golfer;

	private RoundAnalysisByRoundId(String golfer, RoundSummary summary) {
		super(summary);
		this.golfer = golfer;
	}

	static List<RoundAnalysisByRoundId> compile(PerformanceSummaryWithRounds summary) {
		List<RoundAnalysisByRoundId> results = new ArrayList<>();
		for (RoundSummary round : summary.getRoundSummaries()) {
			results.add(new RoundAnalysisByRoundId(summary.getGolfer(), round));
		}
		return results;
	}

}
