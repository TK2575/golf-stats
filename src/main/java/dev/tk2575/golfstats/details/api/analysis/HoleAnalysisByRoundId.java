package dev.tk2575.golfstats.details.api.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class HoleAnalysisByRoundId {
	private final String golfer;
	private final int roundId;
	private final List<HoleAnalysis> holes;

	@JsonIgnore
	public boolean isEmpty() {
		return this.holes == null || this.holes.isEmpty();
	}

	@JsonIgnore public boolean isPresent() {
		return !isEmpty();
	}

	static List<HoleAnalysisByRoundId> compile(PerformanceDetail detail) {
		List<HoleAnalysisByRoundId> results = new ArrayList<>();
		for (RoundDetail round : detail.getRoundDetails()) {
			results.add(new HoleAnalysisByRoundId(detail.getGolfer(), round.getRoundId(), round.getHoles()));
		}
		return results;
	}
}
