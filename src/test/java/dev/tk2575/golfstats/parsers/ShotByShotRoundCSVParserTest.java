package dev.tk2575.golfstats.parsers;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.games.Game;
import dev.tk2575.golfstats.parsers.ShotByShotRoundCSVParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
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
	}

}