package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("analysis")
@Log4j2
public class PerformanceAnalysisApi {

	@GetMapping(produces = "application/json")
	public List<PerformanceSummary> all() {
		return GolfRoundResourceManager.getInstance()
				.getRoundsByGolfer()
				.values()
				.stream()
				.map(PerformanceSummary::new)
				.collect(toList());
	}

	@GetMapping(value = "latestDifferentials", produces = "application/json")
	public Map<String, List<BigDecimal>> latestDifferentials() {
		Map<String, List<BigDecimal>> results = new HashMap<>();

		List<PerformanceSummary> summaries =
				GolfRoundResourceManager.getInstance()
						.getRoundsByGolfer()
						.values()
						.stream()
						.map(PerformanceSummary::new)
						.collect(toList());

		for (PerformanceSummary summary : summaries) {
			List<BigDecimal> differentials =
					GolfRound.stream(summary.getGolfRounds())
							.sortNewestToOldest()
							.limit(20)
							.map(GolfRound::getScoreDifferential)
							.sorted(BigDecimal::compareTo)
							.collect(toList());
			results.put(summary.getGolfer(), differentials);
		}
		return results;
	}
}
