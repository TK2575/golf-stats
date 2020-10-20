package dev.tk2575.golfstats;

import dev.tk2575.golfstats.coursehandicap.CourseHandicap;
import dev.tk2575.golfstats.golferperformance.CurrentGolferStats;
import dev.tk2575.golfstats.golfround.Course;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.SimpleGolfRoundCSVParser;
import dev.tk2575.golfstats.golfround.Tee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class GolfStatsApplication {

	private static final Logger log = LoggerFactory.getLogger(GolfStatsApplication.class);

	//TODO set scale, rounding mode globally (2s for internals, 1 for output)

	public static void main(String[] args) {
		SpringApplication.run(GolfStatsApplication.class, args);

		Map<String, List<GolfRound>> rounds = getCSVData();
		Map<String, CurrentGolferStats> currentStats = computeStatsByGolfer(rounds);

		logStatsAndRoundHistory(currentStats);
		logCourseHandicapForNextRound(currentStats);

		System.exit(0);
	}

	private static Map<String, List<GolfRound>> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		return new SimpleGolfRoundCSVParser(dataDirectory).readCsvData();
	}

	private static Map<String, CurrentGolferStats> computeStatsByGolfer(Map<String, List<GolfRound>> rounds) {
		//TODO one-liner via stream?
		Map<String, CurrentGolferStats> results = new HashMap<>();
		rounds.forEach((k, v) -> results.put(k, new CurrentGolferStats(v)));
		return results;
	}

	private static void logStatsAndRoundHistory(Map<String, CurrentGolferStats> currentStats) {
		currentStats.values().forEach(s -> {
			log.info(s.toString());
			log.info(GolfRound.stream(s.getGolfRounds()).compileTo18HoleRounds().toTSV());
		});
	}

	private static void logCourseHandicapForNextRound(Map<String, CurrentGolferStats> currentStats) {
		String courseName = "Sassy Nine";
		String teeName = "White";
		BigDecimal rating = new BigDecimal("30.7");
		BigDecimal slope = new BigDecimal("111");
		BigDecimal par = new BigDecimal("32");

		if (courseName.isBlank() || teeName.isBlank() || rating.compareTo(BigDecimal.ZERO) <= 0 || slope.compareTo(BigDecimal.ZERO) <= 0 || par
				.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("need to pass some arguments by hand...");
		}

		Course course = Course.newCourse(courseName);
		Tee tee = Tee.newTee(teeName, rating, slope, par.intValue());

		Golfer tom = null, will = null, tomTrend = null, willTrend = null;

		for (CurrentGolferStats stats : currentStats.values()) {
			if (stats.getGolfer().equalsIgnoreCase("tom")) {
				tom = Golfer.of(stats.getGolfer(), stats.getHandicapIndex());
				tomTrend = Golfer.of(stats.getGolfer(), stats.getTrendingHandicap());
			}
			else if (stats.getGolfer().equalsIgnoreCase("will")) {
				will = Golfer.of(stats.getGolfer(), stats.getHandicapIndex());
				willTrend = Golfer.of(stats.getGolfer(), stats.getTrendingHandicap());
			}
		}

		if (tom == null || will == null) {
			throw new IllegalArgumentException("could not find all golfer stats");
		}

		log.info(String.format("Course handicap at %s based on true index", courseName));
		CourseHandicap courseHandicap = CourseHandicap.teamOf(List.of(tom, will), course, tee);
		courseHandicap.getHandicapStrokesPerGolfer().forEach((k,v) -> log.info(String.join(": ", k, v.toString())));
		log.info(String.format("Quota = %s", courseHandicap.getStablefordQuota()));
		//FIXME incorrect for nine hole rounds

		log.info(String.format("Course handicap at %s based on trending index", courseName));
		CourseHandicap courseHandicapFromTrend = CourseHandicap.teamOf(List.of(tomTrend, willTrend), course, tee);
		courseHandicapFromTrend.getHandicapStrokesPerGolfer().forEach((k,v) -> log.info(String.join(": ", k, v.toString())));
		log.info(String.format("Quota = %s", courseHandicapFromTrend.getStablefordQuota()));
	}

}
