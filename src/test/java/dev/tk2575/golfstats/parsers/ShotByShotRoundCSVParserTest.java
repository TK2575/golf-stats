package dev.tk2575.golfstats.parsers;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.games.Game;
import dev.tk2575.golfstats.golfround.holebyhole.Hole;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ShotByShotRoundCSVParserTest {

	@Test
	void testSingleRoundFiles() {
		String roundFileName = "testSingleShotByShotRoundFile.csv";
		String shotByShotFileName = "testSingleShotByShotFile.csv";

		File roundFile = null, shotByShotFile = null;

		File directory = new File(System.getProperty("user.dir"), "src/test/resources/shotByShotCSVParser");
		for (File f : Objects.requireNonNull(directory.listFiles())) {
			if (roundFileName.equalsIgnoreCase(f.getName())) {
				roundFile = f;
			}
			else if (shotByShotFileName.equalsIgnoreCase(f.getName())) {
				shotByShotFile = f;
			}
		}

		assertNotNull(roundFile);
		assertNotNull(shotByShotFile);

		List<GolfRound> rounds = new ShotByShotRoundCSVParser(roundFile, shotByShotFile).parse();
		assertNotNull(rounds);
		assertFalse(rounds.isEmpty());
		assertEquals(1, rounds.size());

		GolfRound round = rounds.get(0);
		assertFalse(round.isNineHoleRound());
		assertEquals(18, round.getHoleCount());
		assertEquals(90, round.getStrokes());
		assertEquals(29, round.getPutts());
		assertEquals(5, round.getFairwaysInRegulation());
		assertEquals(3, round.getGreensInRegulation());
		assertEquals(18, Game.stablefordAllPositive(round).getScore());

	}

	@Test
	void testMultiRoundFiles() {
		String roundFileName = "testMultiShotByShotRoundFile.csv";
		String shotByShotFileName = "testMultiShotByShotFile.csv";

		File roundFile = null, shotByShotFile = null;

		File directory = new File(System.getProperty("user.dir"), "src/test/resources/shotByShotCSVParser");
		for (File f : Objects.requireNonNull(directory.listFiles())) {
			if (roundFileName.equalsIgnoreCase(f.getName())) {
				roundFile = f;
			}
			else if (shotByShotFileName.equalsIgnoreCase(f.getName())) {
				shotByShotFile = f;
			}
		}

		assertNotNull(roundFile);
		assertNotNull(shotByShotFile);

		List<GolfRound> rounds = new ShotByShotRoundCSVParser(roundFile, shotByShotFile).parse();
		assertNotNull(rounds);
		assertFalse(rounds.isEmpty());
		assertEquals(14, rounds.size());

		GolfRound round = rounds.get(2);
		assertTrue(round.isNineHoleRound());
		assertEquals(9, round.getHoleCount());
		assertEquals(42, round.getStrokes());
		assertEquals(18, round.getPutts());
		assertEquals(3, round.getFairwaysInRegulation());
		assertEquals(4, round.getGreensInRegulation());
		assertEquals(12, Game.stablefordAllPositive(round).getScore());

		round = rounds.get(4);
		assertTrue(round.getCourse().getName().startsWith("Red Tail"));
		assertEquals(new BigDecimal("-12.69"), round.getStrokesGained());
		assertEquals(new BigDecimal("-2.40"), round.getHoles().byNumber(8).getStrokesGained());
		assertEquals(new BigDecimal("0.91"), round.getHoles().byNumber(14).getStrokesGained());

		Map<String, BigDecimal> strokesGainedByShotType = round.getHoles().strokesGainedByShotType();
		assertEquals(new BigDecimal("-1.62"), strokesGainedByShotType.get("Tee"));
		assertEquals(new BigDecimal("-2.15"), strokesGainedByShotType.get("Approach"));
		assertEquals(new BigDecimal("-2.96"), strokesGainedByShotType.get("Around Green"));
		assertEquals(new BigDecimal("-5.96"), strokesGainedByShotType.get("Green"));

//		@formatter:off
		rounds.forEach(each -> each.getHoles().forEach(hole -> hole.getShots().forEach(
				shot -> System.out.println(String.join("\t",
						shot.getShotCategory().getLabel(),
						shot.getLie().getAbbrev(),
						shot.getDistance().getValue().toString(),
						shot.getCount().toString(),
						shot.getStrokesGained().toPlainString(),
						each.getDate().format(DateTimeFormatter.ISO_DATE)))
		)));

//		@formatter:on

		//TODO additional tests for strokes gained shots in some other test:
		//strokes gained baseline for hole - strokes = strokes gained in some other test
		//strokes gained by x sums up to total strokes gained
	}

}