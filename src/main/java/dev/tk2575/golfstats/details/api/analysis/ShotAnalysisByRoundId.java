package dev.tk2575.golfstats.details.api.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ShotAnalysisByRoundId {
	private final String golfer;
	private final int roundId;
	private final List<ShotAnalysis> shots;

	@JsonIgnore public boolean isEmpty() {
		return this.shots == null || this.shots.isEmpty();
	}

	@JsonIgnore public boolean isPresent() {
		return !isEmpty();
	}

	static List<ShotAnalysisByRoundId> compile(PerformanceDetail detail) {
		List<ShotAnalysisByRoundId> results = new ArrayList<>();
		for (RoundDetail round : detail.getRoundDetails()) {
			results.add(new ShotAnalysisByRoundId(detail.getGolfer(), round.getRoundId(), round.getShots()));
		}
		return results;
	}
}
