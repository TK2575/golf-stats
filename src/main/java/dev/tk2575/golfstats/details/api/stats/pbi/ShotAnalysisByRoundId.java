package dev.tk2575.golfstats.details.api.stats.pbi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.details.api.stats.PerformanceDetail;
import dev.tk2575.golfstats.details.api.stats.ShotAnalysis;
import lombok.*;

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
		return detail.getRoundDetails().stream().map(round -> new ShotAnalysisByRoundId(detail.getGolfer(), round.getRoundId(), round.getShots())).toList();
	}
}
