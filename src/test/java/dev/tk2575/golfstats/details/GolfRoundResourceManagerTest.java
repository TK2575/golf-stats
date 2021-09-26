package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.parsers.Hole19JsonParser;
import dev.tk2575.golfstats.details.parsers.Hole19Round;
import dev.tk2575.golfstats.details.parsers.SimpleGolfRoundCSVParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;

class GolfRoundResourceManagerTest {

	@Test
	void testJoin() {
		List<CSVFile> files = readCSVFilesInDirectory("data/simple");
		assertFalse(files.isEmpty());
		List<GolfRound> simpleRounds = new SimpleGolfRoundCSVParser(files).parse();
		assertFalse(simpleRounds.isEmpty());

		List<Hole19Round> hole19Rounds = Hole19JsonParser.parse("data/hole19/hole19_export-tom.json");
		assertFalse(hole19Rounds.isEmpty());

		List<GolfRound> joinRounds = GolfRoundResourceManager.join(simpleRounds, hole19Rounds);
		assertFalse(joinRounds.isEmpty());
		Map<String, GolfRound> joinRoundsMap = joinRounds.stream().collect(toMap(this::roundKey, identity()));

		GolfRound join;
		for (GolfRound each : simpleRounds) {
			String roundKey = roundKey(each);
			System.out.println(roundKey);

			join = joinRoundsMap.get(roundKey);
			assertNotNull(join);
			assertFalse(join.getHoles().isEmpty());
			assertEquals(each.getStrokes(), join.getStrokes());
		}


	}

	private String roundKey(GolfRound round) {
		return String.join("-", round.getDate().toString(), round.getCourse().getName(), round.getDuration().toString());
	}

}