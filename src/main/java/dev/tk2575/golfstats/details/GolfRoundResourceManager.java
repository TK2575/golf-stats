package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.details.parsers.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.IntStream;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static java.util.Comparator.comparing;
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

		return rounds.stream().sorted(comparing((GolfRound each) -> each.getGolfer().getName()).thenComparing(GolfRound::getDate)).toList();
	}

	static List<GolfRound> join(@NonNull List<GolfRound> simpleRounds, @NonNull List<Hole19Round> hole19Rounds) {
		if (hole19Rounds.isEmpty()) {
			return simpleRounds;
		}

		Map<String, List<Hole19Round>> hole19Map =
				hole19Rounds.stream()
						.filter(each -> !each.getHoles().isEmpty())
						.collect(groupingBy(GolfRoundResourceManager::roundKey));

		List<GolfRound> results = new ArrayList<>();
		List<Hole19Round> roundList;
		String key;

		for (GolfRound simpleRound : simpleRounds) {
			key = roundKey(simpleRound);
			roundList = hole19Map.get(key);

			if (isValid(roundList, key)) {
				results.add(GolfRound.of(new RoundMeta(simpleRound), cleanHoleData(roundList)));
				hole19Map.remove(key);
			}
			else {
				results.add(simpleRound);
			}
		}

		//TODO what's left in hole19Map?
		log.info("Found the following rounds orphaned in Hole 19 export");
		hole19Map.values().stream().flatMap(Collection::stream).forEach(each -> log.info(roundKey(each)));

		return results;

	}

	private static boolean isValid(List<Hole19Round> roundList, String key) {
		if (roundList == null) {
			log.warn(String.format("Could not find Hole19 data for %s", key));
			return false;
		}
		if (roundList.size() > 2) {
			log.warn(String.format("Found more than two rounds for %s, skipping", key));
			return false;
		}

		long holeCount = roundList.stream().map(Hole19Round::getHoles).mapToLong(Collection::size).sum();
		if (holeCount == 0) {
			log.warn(String.format("Hole19 export did not include any hole data for %s", key));
			return false;
		}
		if (holeCount > 18) {
			log.warn(String.format("Found more than 18 holes for %s, skipping", key));
			return false;
		}
		return true;
	}

	private static List<Hole> cleanHoleData(List<Hole19Round> input) {
		List<Hole> holes = new ArrayList<>(input.stream().map(Hole19Round::getHoles).flatMap(Collection::stream).toList());
		if (holes.size() > 9 && holes.size() < 18) {
			holes = new ArrayList<>(holes.subList(0,9));
		}
		if (new HoleStream(holes).duplicateNumbers()) {
			holes = reassignHoleNumbers(holes);
		}
		if (holes.stream().map(Hole::getIndex).anyMatch(index -> index == 0)) {
			holes = randomlyAssignIndexes(holes);
		}
		if (new HoleStream(holes).duplicateIndexes()) {
			holes = new HoleStream(holes).shuffleEighteenHoleIndexes().toList();
		}
		return holes;
	}

	private static String roundKey(Hole19Round round) {
		return String.join("-", round.getStartedAt().toLocalDate().toString(), round.getCourse());
	}

	private static String roundKey(GolfRound round) {
		return String.join("-", round.getDate().toString(), round.getCourse().getName());
	}

	private static List<Hole> reassignHoleNumbers(List<Hole> holes) {
		List<Hole> results = new ArrayList<>(holes.subList(0,9).stream().toList());
		results.addAll(
				holes.subList(9,holes.size()).stream()
						.map(each -> each.getNumber() < 10 ? each.setNumber(each.getNumber() + 9) : each)
						.toList()
		);
		return results;
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
