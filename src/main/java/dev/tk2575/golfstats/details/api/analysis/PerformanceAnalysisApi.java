package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("analysis")
@Log4j2
public class PerformanceAnalysisApi {

	@GetMapping(produces = "application/json")
	public List<PerformanceSummary> all() {
		//TODO take list of rounds and apply double bogey to them from first to last, updating handicap index after each
		// add resultant handicap index to roundsummary

		return GolfRoundResourceManager.getInstance()
				.getRoundsByGolfer()
				.values()
				.stream()
				.map(PerformanceSummary::new)
				.collect(toList());
	}
}
