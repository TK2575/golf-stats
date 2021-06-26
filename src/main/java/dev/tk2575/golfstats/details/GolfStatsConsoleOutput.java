package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.PerformanceSummary;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.handicapindex.StablefordQuota;
import dev.tk2575.golfstats.details.parsers.HoleByHoleRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.ShotByShotRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.SimpleGolfRoundCSVParser;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class GolfStatsConsoleOutput implements Runnable {

	//TODO set scale, rounding mode globally (2s for internals, 1 for output)

	@Override
	public void run() {
		Map<String, List<GolfRound>> roundsByGolfer = parseCsvResources();

		List<PerformanceSummary> currentStats = computeStatsByGolfer(roundsByGolfer);
		logStatsAndRoundHistory(currentStats);
//		logCourseHandicapForNextRound(currentStats);
	}

	private void logShotsGainedInfo(List<GolfRound> rounds) {
		//TODO move to PerformanceSummary
		SortedMap<LocalDate, List<Shot>> shotsByDate = new TreeMap<>();
		rounds.forEach(each -> shotsByDate.put(each.getDate(), each.getHoles().allShots().categorized().asList()));

		log.info(String.join("\t", "Index", "Date", "Lie", "Category", "Distance Value", "Distance Unit", "Strokes Gained"));
		int i = 0;
		for (Map.Entry<LocalDate, List<Shot>> entry : shotsByDate.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				i++;
				for (Shot shot : entry.getValue()) {
					log.info(String.join("\t",
							Integer.toString(i),
							entry.getKey().toString(),
							shot.getLie().getLabel(),
							shot.getShotCategory().getLabel(),
							Long.toString(shot.getDistance().getValue()),
							shot.getDistance().getLengthUnit(),
							shot.getStrokesGained().toPlainString()));
				}
			}
		}
	}

	private static Map<String, List<GolfRound>> parseCsvResources() {
		//TODO stream as resources
		//TODO cleanup CSV parser method accessibility, parse() vs parseFile() approach
		//TODO separate reading golf round results from applying net double bogey (do that in PerformanceSummary)
		// then take list of rounds and apply double bogey to them from first to last
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		Map<String, List<GolfRound>> simpleRounds = new SimpleGolfRoundCSVParser(new File(dataDirectory, "simple")).readCsvData();

		List<GolfRound> holeByHoleRounds = new ArrayList<>();
		File holeByHoleDirectory = new File(dataDirectory, "hole-by-hole");
		if (holeByHoleDirectory != null && holeByHoleDirectory.list().length > 0) {
			File rounds = new File(holeByHoleDirectory, "rounds.csv");
			File holes = new File(holeByHoleDirectory, "holes.csv");

			if (rounds.exists() && holes.exists()) {
				holeByHoleRounds = new HoleByHoleRoundCSVParser(rounds, holes).parse();
			}
		}

		List<GolfRound> shotByShotRounds = new ArrayList<>();
		File shotByShotDirectory = new File(dataDirectory, "shot-by-shot");
		if (shotByShotDirectory != null && shotByShotDirectory.list().length > 0) {
			File rounds = new File(shotByShotDirectory, "rounds.csv");
			File shots = new File(shotByShotDirectory, "shots.csv");

			if (rounds.exists() && shots.exists()) {
				shotByShotRounds = new ShotByShotRoundCSVParser(rounds, shots).parse();
			}
		}

		List<GolfRound> allRounds = new ArrayList<>();
		/*for (List<GolfRound> list : simpleRounds.values()) {
			allRounds.addAll(list);
		}
		allRounds.addAll(holeByHoleRounds);*/
		allRounds.addAll(shotByShotRounds);

		return allRounds.stream().collect(Collectors.groupingBy(each -> each.getGolfer().getName()));
	}

	private static List<PerformanceSummary> computeStatsByGolfer(Map<String, List<GolfRound>> rounds) {
		//TODO one-liner via stream?
		//TODO add shots gained results to Performance Summary?
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
		//TODO move to separate CourseHandicapCalculator manager object
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
