package dev.tk2575.golfstats.details.api.stats.pbi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.details.api.stats.HoleAnalysis;
import dev.tk2575.golfstats.details.api.stats.PerformanceDetail;
import lombok.*;

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
		return detail.getRoundDetails().stream().map(round -> new HoleAnalysisByRoundId(detail.getGolfer(), round.getRoundId(), round.getHoles())).toList();
	}
}
