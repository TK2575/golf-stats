package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.parsers.HoleByHoleRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.ShotByShotRoundCSVParser;
import dev.tk2575.golfstats.details.parsers.SimpleGolfRoundCSVParser;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static java.util.stream.Collectors.groupingBy;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GolfRoundResourceManager {

	public static GolfRoundResourceManager getInstance() {
		return instance;
	}

	public Map<String, List<GolfRound>> getRoundsByGolfer() {
		return this.golfRoundsFromFiles.stream().collect(groupingBy(each -> each.getGolfer().getName()));
	}

	private static final GolfRoundResourceManager instance = new GolfRoundResourceManager();

	private final List<GolfRound> golfRoundsFromFiles = readGolfRoundsFromFiles();

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
}
