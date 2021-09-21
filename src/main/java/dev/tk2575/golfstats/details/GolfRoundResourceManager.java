package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.details.parsers.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static java.util.stream.Collectors.groupingBy;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class GolfRoundResourceManager {

	public static GolfRoundResourceManager getInstance() {
		return instance;
	}

	public Map<String, List<GolfRound>> getRoundsByGolfer() {
		return this.golfRoundsFromFiles.stream().collect(groupingBy(each -> each.getGolfer().getName()));
	}

	private static final GolfRoundResourceManager instance = new GolfRoundResourceManager();

	//TODO support changes to file without requiring restart
	private final List<GolfRound> golfRoundsFromFiles = readGolfRoundsFromFiles();

	private List<GolfRound> readGolfRoundsFromFiles() {
		List<CSVFile> files = readCSVFilesInDirectory("data/simple");
		List<GolfRound> simpleRounds = new ArrayList<>();
		if (!files.isEmpty()) {
			simpleRounds = new SimpleGolfRoundCSVParser(files).parse();
		}

		List<GolfRound> rounds = new ArrayList<>(join(simpleRounds, Hole19JsonParser.parse("data/hole19/hole19_export-tom.json")));

		files = readCSVFilesInDirectory("data/hole-by-hole");
		if (!files.isEmpty()) {
			rounds.addAll(new HoleByHoleRoundCSVParser(files).parse());
		}

		files = readCSVFilesInDirectory("data/shot-by-shot");
		if (!files.isEmpty()) {
			rounds.addAll(new ShotByShotRoundCSVParser(files).parse());
		}

		return rounds.stream().sorted(Comparator.comparing((GolfRound each) -> each.getGolfer().getName()).thenComparing(GolfRound::getDate)).toList();
	}

	private List<GolfRound> join(@NonNull List<GolfRound> simpleRounds, @NonNull List<Hole19Round> hole19Rounds) {
		if (hole19Rounds.isEmpty()) {
			return simpleRounds;
		}

		List<GolfRound> results = new ArrayList<>();
		GolfRound simpleRound;
		Hole19Round hole19Round;
		//expects arguments to already be sorted
		int i = 0, j = 0;
		while (i < simpleRounds.size()) {
			simpleRound = simpleRounds.get(i);

			if (j < hole19Rounds.size()) {
				hole19Round = hole19Rounds.get(j);

				if (hole19Round.sameAs(simpleRound)) {
					results.add(GolfRound.of(new RoundMeta(simpleRound), hole19Round.getHoles()));
					i++;
					j++;
				}
				else {
					if (simpleRound.getDate().isAfter(hole19Round.getStartedAt().toLocalDate())) {
						i++;
					}
					else {
						j++;
					}
				}
			}
			else {
				results.add(simpleRound);
				i++;
			}
		}

		return results;

	}
}
