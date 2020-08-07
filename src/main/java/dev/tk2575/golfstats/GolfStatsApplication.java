package dev.tk2575.golfstats;

import dev.tk2575.golfstats.golfround.GolfRound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
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
		currentStats.values().forEach(s -> {
			log.info(s.toString());
			log.info(String.format("Rounds used to compute %s's Handicap Index: \n%s", s.getGolfer(), s.currentRoundsToCSV()));
		});

		System.exit(0);
	}



	public static Map<String, CurrentGolferStats> computeStatsByGolfer(Map<String, List<GolfRound>> rounds) {
		Map<String, CurrentGolferStats> results = new HashMap<>();
		rounds.forEach((k,v) -> results.put(k, new CurrentGolferStats(k, v)));
		return results;
	}

	public static Map<String, List<GolfRound>> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		return GolfRoundCSVParser.readCsvData(dataDirectory);
	}

}
