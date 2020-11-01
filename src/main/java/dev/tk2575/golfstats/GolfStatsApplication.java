package dev.tk2575.golfstats;

import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.golferperformance.CurrentGolferStats;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.SimpleGolfRoundCSVParser;
import dev.tk2575.golfstats.handicapindex.StablefordQuota;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class GolfStatsApplication {

	private static final Logger log = LoggerFactory.getLogger(GolfStatsApplication.class);

	//TODO set scale, rounding mode globally (2s for internals, 1 for output)

	public static void main(String[] args) {
		SpringApplication.run(GolfStatsApplication.class, args);

		Map<String, List<GolfRound>> rounds = getCSVData();
		List<CurrentGolferStats> currentStats = computeStatsByGolfer(rounds);

		logStatsAndRoundHistory(currentStats);
		logCourseHandicapForNextRound(currentStats);

		System.exit(0);
	}

	private static Map<String, List<GolfRound>> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		return new SimpleGolfRoundCSVParser(dataDirectory).readCsvData();
	}

	private static List<CurrentGolferStats> computeStatsByGolfer(Map<String, List<GolfRound>> rounds) {
		//TODO one-liner via stream?
		List<CurrentGolferStats> results = new ArrayList<>();
		rounds.forEach((k, v) -> results.add(new CurrentGolferStats(v)));
		return results;
	}

	private static void logStatsAndRoundHistory(List<CurrentGolferStats> currentStats) {
		currentStats.forEach(s -> {
			log.info(s.getGolfer());
			log.info(GolfRound.stream(s.getGolfRounds())
			                  .compileTo18HoleRounds()
			                  .sortNewestToOldest()
			                  .limit(20)
			                  .toTSV());
		});
	}

	private static void logCourseHandicapForNextRound(List<CurrentGolferStats> currentStats) {
		Tee back = Tee.of("Back", new BigDecimal("70.4"), new BigDecimal("122"), 35, 3189L);

		Golfer tom = null, tomTrend = null, tomAnti = null, will = null, willTrend = null, willAnti = null;

		for (CurrentGolferStats stats : currentStats) {
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

		StablefordQuota highQuota = back.stablefordQuota(List.of(tom, will));
		StablefordQuota trendQuota = back.stablefordQuota(List.of(tomTrend, willTrend));
		StablefordQuota lowQuota = back.stablefordQuota(List.of(tomAnti, willAnti));

		log.info(String.format("High quota = %s, (%s)", highQuota.getTotalQuota(), highQuota.getTee().getHandicapStrokes()));
		log.info(String.format("Trend quota = %s, (%s)", trendQuota.getTotalQuota(), trendQuota.getTee().getHandicapStrokes()));
		log.info(String.format("Low quota = %s, (%s)", lowQuota.getTotalQuota(), lowQuota.getTee().getHandicapStrokes()));
	}

}
