package dev.tk2575.golfstats.details.api.stats.pbi;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import dev.tk2575.golfstats.details.api.stats.PerformanceDetail;
import dev.tk2575.golfstats.details.api.stats.PerformanceSummaryWithRounds;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("stats/pbi")
@Log4j2
public class PBIStatsApi {

	@GetMapping(value = "rounds", produces = "application/json")
	public List<RoundAnalysisByRoundId> rounds() {
		/*@formatter:off*/
		return golfRoundListStream()
				.map(PerformanceSummaryWithRounds::new)
				.map(RoundAnalysisByRoundId::compile)
				.flatMap(List::stream)
				.toList();
		/*@formatter:on*/
	}

	@GetMapping(value = "holes", produces = "application/json")
	public List<HoleAnalysisByRoundId> holes() {
		/*@formatter:off*/
		return golfRoundListStream()
				.map(PerformanceDetail::new)
				.map(HoleAnalysisByRoundId::compile)
				.flatMap(List::stream)
				.filter(HoleAnalysisByRoundId::isPresent)
				.toList();
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
				.toList();
		/*@formatter:on*/
	}

	private Stream<List<GolfRound>> golfRoundListStream() {
		/*@formatter:off*/
		return GolfRoundResourceManager.getInstance()
				.getRoundsByGolfer()
				.values()
				.stream();
		/*@formatter:on*/
	}
}
