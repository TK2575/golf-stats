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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
	public List<RoundAnalysisByRoundId> rounds() {
		return golfRoundListStream()
				.map(PerformanceSummaryWithRounds::new)
				.map(RoundAnalysisByRoundId::compile)
				.flatMap(List::stream)
				.collect(toList());
	}

	@GetMapping(value = "holes", produces = "application/json")
	public List<HoleAnalysisByRoundId> holes() {
		/*@formatter:off*/
		return golfRoundListStream()
				.map(PerformanceDetail::new)
				.map(HoleAnalysisByRoundId::compile)
				.flatMap(List::stream)
				.filter(HoleAnalysisByRoundId::isPresent)
				.collect(toList());
		/*@formatter:on*/
	}

	@GetMapping(value = "shots", produces = "application/json")
	public List<ShotAnalysisByRoundId> shots() {
		/*@formatter:off*/
		return golfRoundListStream()
				.map(PerformanceDetail::new)
				.map(ShotAnalysisByRoundId::compile)
				.flatMap(List::stream)
				.filter(ShotAnalysisByRoundId::isPresent)
				.collect(toList());
		/*@formatter:on*/
	}
}
