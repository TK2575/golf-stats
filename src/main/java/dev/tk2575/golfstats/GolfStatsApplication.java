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
//		logCourseHandicapForNextRound(currentStats);

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
			log.info(String.format("%s (%s)", s.getGolfer(), s.getHandicapIndex().getValue().toPlainString()));
			log.info(GolfRound.stream(s.getGolfRounds())
			                  .compileTo18HoleRounds()
			                  .sortNewestToOldest()
			                  .limit(20)
			                  .toTSV());
		});
	}

	private static void logCourseHandicapForNextRound(List<CurrentGolferStats> currentStats) {
		Tee blue = Tee.of("Blue", new BigDecimal("31.5"), new BigDecimal("114"), 32, 2383L);
		Tee white = Tee.of("White", new BigDecimal("30.7"), new BigDecimal("111"), 32, 2167L);

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

		StablefordQuota blueHighQuota = blue.stablefordQuota(List.of(tom, will));
		StablefordQuota blueTrendQuota = blue.stablefordQuota(List.of(tomTrend, willTrend));
		StablefordQuota blueLowQuota = blue.stablefordQuota(List.of(tomAnti, willAnti));

		log.info(String.format("%s - High quota = %s, (%s)", blueHighQuota.getTee().getName(), blueHighQuota.getTotalQuota(), blueHighQuota.getTee().getHandicapStrokes()));
		log.info(String.format("%s - Trend quota = %s, (%s)", blueTrendQuota.getTee().getName(), blueTrendQuota.getTotalQuota(), blueTrendQuota.getTee().getHandicapStrokes()));
		log.info(String.format("%s - Low quota = %s, (%s)", blueLowQuota.getTee().getName(), blueLowQuota.getTotalQuota(), blueLowQuota.getTee().getHandicapStrokes()));

		StablefordQuota whiteHighQuota = white.stablefordQuota(List.of(tom, will));
		StablefordQuota whiteTrendQuota = white.stablefordQuota(List.of(tomTrend, willTrend));
		StablefordQuota whiteLowQuota = white.stablefordQuota(List.of(tomAnti, willAnti));

		log.info(String.format("%s - High quota = %s, (%s)", whiteHighQuota.getTee().getName(), whiteHighQuota.getTotalQuota(), whiteHighQuota.getTee().getHandicapStrokes()));
		log.info(String.format("%s - Trend quota = %s, (%s)", whiteTrendQuota.getTee().getName(), whiteTrendQuota.getTotalQuota(), whiteTrendQuota.getTee().getHandicapStrokes()));
		log.info(String.format("%s - Low quota = %s, (%s)", whiteLowQuota.getTee().getName(), whiteLowQuota.getTotalQuota(), whiteLowQuota.getTee().getHandicapStrokes()));
	}

}
