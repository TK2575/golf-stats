package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping("analysis")
@Log4j2
public class PerformanceAnalysisApi {

	private Stream<List<GolfRound>> golfRoundListStream() {
		return GolfRoundResourceManager.getInstance()
				.getRoundsByGolfer()
				.values()
				.stream();
	}

	@GetMapping(produces = "application/json")
	public List<PerformanceDetail> all() {
		return golfRoundListStream().map(PerformanceDetail::new).collect(toList());
	}

	@GetMapping(value = "latestDifferentials", produces = "application/json")
	public Map<String, List<BigDecimal>> latestDifferentials() {
		Map<String, List<BigDecimal>> results = new HashMap<>();

		golfRoundListStream().map(PerformanceSummary::new).forEach(summary -> {
			List<BigDecimal> differentials =
					GolfRound.stream(summary.getGolfRounds())
							.sortNewestToOldest()
							.limit(20)
							.map(GolfRound::getScoreDifferential)
							.sorted(BigDecimal::compareTo)
							.collect(toList());
			results.put(summary.getGolfer(), differentials);
		});

		return results;
	}

	@GetMapping(value = "rounds", produces = "application/json")
	public Map<String, List<RoundSummary>> rounds() {
		return golfRoundListStream()
				.map(PerformanceSummaryWithRounds::new)
				.collect(toMap(PerformanceSummaryWithRounds::getGolfer, PerformanceSummaryWithRounds::getRoundSummaries));
	}

	@GetMapping(value = "holes", produces = "application/json")
	public Map<String, Map<String, List<HoleAnalysis>>> holes() {
		Map<String, Map<String, List<HoleAnalysis>>> results = new HashMap<>();
		golfRoundListStream().map(PerformanceDetail::new).forEach(each -> {
			Map<String, List<HoleAnalysis>> holesByRound =
					each.getRoundDetails().stream().collect(toMap(RoundDetail::getDisplay, RoundDetail::getHoles));
			results.put(each.getGolfer(), holesByRound);
		});
		return results;
	}

	@GetMapping(value = "shots", produces = "application/json")
	public Map<String, List<ShotAnalysis>> shots() {
		Map<String, List<ShotAnalysis>> results = new HashMap<>();

		for (PerformanceDetail detail : golfRoundListStream().map(PerformanceDetail::new).collect(toList())) {
			List<ShotAnalysis> shots = detail.getRoundDetails().stream().map(RoundDetail::getShots).flatMap(List::stream).collect(toList());
			results.put(detail.getGolfer(), shots);
		}

		return results;
	}
}
