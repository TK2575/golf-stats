package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.PerformanceSummary;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.handicapindex.StablefordQuota;
import dev.tk2575.golfstats.details.parsers.HoleByHoleRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.ShotByShotRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.SimpleGolfRoundCSVParser;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static dev.tk2575.Utils.readCSVFilesInDirectory;

@Log4j2
public class GolfStatsConsoleOutput implements Runnable {

	@Override
	public void run() {
		try {
			List<GolfRound> simpleRounds = new SimpleGolfRoundCSVParser((readCSVFilesInDirectory("data/simple"))).parse();
			List<GolfRound> holeByHoleRounds = new HoleByHoleRoundCSVParser(readCSVFilesInDirectory("data/hole-by-hole")).parse();
			List<GolfRound> shotByShotRounds = new ShotByShotRoundCSVParser(readCSVFilesInDirectory("data/shot-by-shot")).parse();

			//TODO separate reading golf round results from applying net double bogey (do that in PerformanceSummary)
			// then take list of rounds and apply double bogey to them from first to last, updating handicap index after each
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	//TODO move to PerformanceSummary
	private void logShotsGainedInfo(List<GolfRound> rounds) {
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

	//TODO move to separate CourseHandicapCalculator object
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

	//local testing
	public static void main(String[] args) {
		new GolfStatsConsoleOutput().run();
	}
}
