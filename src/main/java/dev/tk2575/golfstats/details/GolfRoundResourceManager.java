package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.details.parsers.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static java.util.stream.Collectors.counting;
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

	static List<GolfRound> join(@NonNull List<GolfRound> simpleRounds, @NonNull List<Hole19Round> hole19Rounds) {
		if (hole19Rounds.isEmpty()) {
			return simpleRounds;
		}

		List<GolfRound> results = new ArrayList<>();
		List<Hole> partialRound = null;
		GolfRound simpleRound;
		Hole19Round hole19Round;
		//expects arguments to already be sorted
		int i = 0, j = 0;
		while (i < simpleRounds.size()) {
			simpleRound = simpleRounds.get(i);
			String logMessage = String.join("-", simpleRound.getDate().toString(), simpleRound.getCourse().getName());
			log.debug(logMessage);

			if ("2016-05-22-Oaks North (East/South)".equals(logMessage)) {
				log.debug("found it");
			}

			if (j < hole19Rounds.size()) {
				hole19Round = hole19Rounds.get(j);
				if (hole19Round.getHoles().isEmpty()) {
					j++;
				}

				else if (hole19Round.sameAs(simpleRound)) {
					List<Hole> holes = cleanHolesData(hole19Round);

					if (!simpleRound.isNineHoleRound() && hole19Round.isNineHoleRound()) {
						if (partialRound == null) {
							partialRound = new ArrayList<>(Hole.stream(holes).shuffleNineHoleIndexesOdd().toList());
						}
						else {
							partialRound.addAll(Hole.stream(holes).shuffleNineHoleIndexesEven().toList());
						}
						if (partialRound.size() == 18) {
							results.add(GolfRound.of(new RoundMeta(simpleRound), partialRound));
							i++;
						}
					}
					else {
						results.add(GolfRound.of(new RoundMeta(simpleRound), holes));
						i++;
					}
					j++;
				}
				else {
					if (simpleRound.getDate().isBefore(hole19Round.getStartedAt().toLocalDate())) {
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

	private static List<Hole> cleanHolesData(Hole19Round hole19Round) {
		List<Hole> holes = hole19Round.getHoles();

		if (holes.size() > 9 && holes.size() < 18) {
			holes = new ArrayList<>(holes.stream().limit(9).toList());
		}

		if (holes.stream().map(Hole::getIndex).anyMatch(index -> index == 0)) {
			holes = randomlyAssignIndexes(holes);
		}

		if (new HoleStream(holes).duplicateIndexes()) {
			holes = new HoleStream(holes).shuffleEighteenHoleIndexes().toList();
		}
		return holes;
	}

	private static List<Hole> randomlyAssignIndexes(List<Hole> holes) {
		List<Integer> indexes = new ArrayList<>(IntStream.rangeClosed(1, 9).boxed().toList());
		Collections.shuffle(indexes);

		if (holes.size() == 18) {
			List<Integer> secondNine = new ArrayList<>(IntStream.rangeClosed(10, 18).boxed().toList());
			Collections.shuffle(secondNine);
			indexes.addAll(secondNine);
		}

		List<Hole> results = new ArrayList<>();
		int i = 0;
		for (Hole hole : holes) {
			results.add(hole.setIndex(indexes.get(i)));
			i++;
		}
		return results;
	}
}
