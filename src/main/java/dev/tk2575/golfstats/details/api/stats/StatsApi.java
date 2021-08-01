package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("stats")
@Log4j2
public class StatsApi {

	@GetMapping(produces = "application/json")
	public List<PerformanceDetail> stats() {
		/*@formatter:off*/
		return GolfRoundResourceManager.getInstance()
				.getRoundsByGolfer()
				.values()
				.stream()
				.map(PerformanceDetail::new)
				.toList();
		/*@formatter:on*/
	}
}
