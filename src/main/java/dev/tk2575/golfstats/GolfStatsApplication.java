package dev.tk2575.golfstats;

import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.handicapindex.StablefordQuota;
import dev.tk2575.golfstats.parsers.SimpleGolfRoundCSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class GolfStatsApplication {

	private static final Logger log = LoggerFactory.getLogger(GolfStatsApplication.class);

	//TODO set scale, rounding mode globally (2s for internals, 1 for output)

	public static void main(String[] args) {
		SpringApplication.run(GolfStatsApplication.class, args);

		Map<String, List<GolfRound>> rounds = getCSVData();
		List<PerformanceSummary> currentStats = computeStatsByGolfer(rounds);

		logStatsAndRoundHistory(currentStats);
		//		logCourseHandicapForNextRound(currentStats);

		System.exit(0);
	}

	private static Map<String, List<GolfRound>> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		return new SimpleGolfRoundCSVParser(dataDirectory).readCsvData();
	}

	private static List<PerformanceSummary> computeStatsByGolfer(Map<String, List<GolfRound>> rounds) {
		//TODO one-liner via stream?
		List<PerformanceSummary> results = new ArrayList<>();
		rounds.forEach((k, v) -> results.add(new PerformanceSummary(v)));
		return results;
	}

	private static void logStatsAndRoundHistory(List<PerformanceSummary> currentStats) {
		currentStats.forEach(s -> {
			log.info(String.format("%s (%s)", s.getGolfer(), s.getHandicapIndex().getValue().toPlainString()));
			Map<String, Long> roundsByCourse = s.getGolfRounds()
			                                    .stream()
			                                    .filter(round -> round.getDate().getYear() == 2020)
			                                    .collect(Collectors.groupingBy(round -> round.getCourse()
			                                                                                 .getName(), Collectors.counting()));

			log.info(roundsByCourse.toString());
		});
	}

	private static void logCourseHandicapForNextRound(List<PerformanceSummary> currentStats) {
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
	}

}
