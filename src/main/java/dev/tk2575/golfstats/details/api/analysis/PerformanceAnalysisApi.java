package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.CSVFile;
import dev.tk2575.golfstats.details.parsers.HoleByHoleRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.ShotByShotRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.SimpleGolfRoundCSVParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("analysis")
@Log4j2
public class PerformanceAnalysisApi {

	@GetMapping(produces = "application/json")
	public List<PerformanceSummary> all() {
		//TODO separate reading golf round results from applying net double bogey (do that in PerformanceSummary)
		// then take list of rounds and apply double bogey to them from first to last, updating handicap index after each

		return readGolfRoundsFromFiles()
				.stream()
				.collect(groupingBy(each -> each.getGolfer().getName()))
				.values()
				.stream()
				.map(PerformanceSummary::new)
				.collect(toList());
	}

	private List<GolfRound> readGolfRoundsFromFiles() {
		List<GolfRound> rounds = new ArrayList<>();
		List<CSVFile> files = readCSVFilesInDirectory("data/simple");
		if (!files.isEmpty()) {
			rounds.addAll(new SimpleGolfRoundCSVParser(files).parse());
		}

		files = readCSVFilesInDirectory("data/hole-by-hole");
		if (!files.isEmpty()) {
			rounds.addAll(new HoleByHoleRoundCSVParser(files).parse());
		}

		files = readCSVFilesInDirectory("data/shot-by-shot");
		if (!files.isEmpty()) {
			rounds.addAll(new ShotByShotRoundCSVParser(files).parse());
		}
		return rounds;
	}

	//TODO move to separate CourseHandicapCalculator object
	/*private static void logCourseHandicapForNextRound(List<PerformanceSummary> currentStats) {
		Tee back = Tee.of("White", new BigDecimal("70.4"), new BigDecimal("122"), 35);

		Golfer tom = null, tomTrend = null, tomAnti = null, will = null, willTrend = null, willAnti = null;

		for (PerformanceSummary stats : currentStats) {
			if (stats.getGolfer().equalsIgnoreCase("tom")) {
				tom = Golfer.of(stats.getGolfer(), stats.getHandicapIndex());
				tomTrend = Golfer.of(stats.getGolfer(), stats.getTrendingHandicap());
				tomAnti = Golfer.of(stats.getGolfer(), stats.getAntiHandicap());

			}
			else if (stats.getGolfer().equalsIgnoreCase("will")) {
				will = Golfer.of(stats.getGolfer(), stats.getHandicapIndex());
				willTrend = Golfer.of(stats.getGolfer(), stats.getTrendingHandicap());
				willAnti = Golfer.of(stats.getGolfer(), stats.getTrendingHandicap());
			}
		}

		if (tom == null || will == null) {
			throw new IllegalArgumentException("could not find all golfer stats");
		}

		StablefordQuota whiteHighQuota = back.stablefordQuota(List.of(tom, will));
		StablefordQuota whiteTrendQuota = back.stablefordQuota(List.of(tomTrend, willTrend));
		StablefordQuota whiteLowQuota = back.stablefordQuota(List.of(tomAnti, willAnti));

		log.info(String.format("%s - High quota = %s, (%s)", whiteHighQuota.getTee()
				.getName(), whiteHighQuota.getTotalQuota(), whiteHighQuota
				.getTee()
				.getHandicapStrokes()));
		log.info(String.format("%s - Trend quota = %s, (%s)", whiteTrendQuota.getTee()
				.getName(), whiteTrendQuota.getTotalQuota(), whiteTrendQuota
				.getTee()
				.getHandicapStrokes()));
		log.info(String.format("%s - Low quota = %s, (%s)", whiteLowQuota.getTee()
				.getName(), whiteLowQuota.getTotalQuota(), whiteLowQuota
				.getTee()
				.getHandicapStrokes()));
	}*/
}
