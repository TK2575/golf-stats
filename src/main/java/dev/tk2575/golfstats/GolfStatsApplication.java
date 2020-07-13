package dev.tk2575.golfstats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class GolfStatsApplication {

	private static final Logger log = LoggerFactory.getLogger(GolfStatsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GolfStatsApplication.class, args);

		List<GolfRound> rounds = getCSVData();

		List<GolfRoundAnalysis> golfRoundAnalyses = new ArrayList<>();
		for (GolfRound round : rounds) {
			golfRoundAnalyses.add(new GolfRoundAnalysis(round));
		}

		golfRoundAnalyses.stream().filter(r -> r.getData().getGolfer().equalsIgnoreCase("tom"))
				.forEach(r -> log.info(r.getData().getDate().toString() + ", " + r.getData().getNineHoleRound() + ", " + r.getScoreDifferential()));

//		outputResults();

		System.exit(0);
	}

	private static List<GolfRound> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		return GolfRoundCSVParser.readCsvData(dataDirectory);
	}

}
