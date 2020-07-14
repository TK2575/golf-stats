package dev.tk2575.golfstats;

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

	public static void main(String[] args) {
		SpringApplication.run(GolfStatsApplication.class, args);

		Map<String, List<GolfRound>> rounds = getCSVData();
		Map<String, CurrentGolferStats> currentStats = computeStatsByGolfer(rounds);
		currentStats.values().forEach(s -> log.info(s.toString()));

		System.exit(0);
	}

	private static Map<String, CurrentGolferStats> computeStatsByGolfer(Map<String, List<GolfRound>> rounds) {
		Map<String, CurrentGolferStats> results = new HashMap<>();
		for (Map.Entry<String, List<GolfRound>> golferRounds : rounds.entrySet()) {
			CurrentGolferStats stats = new CurrentGolferStats(golferRounds.getKey(), golferRounds.getValue());
			results.put(golferRounds.getKey(), stats);
		}
		return results;
	}

	private static Map<String, List<GolfRound>> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		return GolfRoundCSVParser.readCsvData(dataDirectory);
	}

}
