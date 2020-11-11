package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.games.Game;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class HoleByHoleRoundCSVParserTest {

	@Test
	void testSingleRoundFiles() {

		String roundFileName = "testHoleByHoleRoundFile.csv";
		String holeByHoleFileName = "testHoleByHoleFile.csv";

		File roundFile = null, holeByHoleFile = null;

		File directory = new File(System.getProperty("user.dir"), "src/test/resources/holeByHoleCSVParser");
		for (File f : Objects.requireNonNull(directory.listFiles())) {
			if (roundFileName.equalsIgnoreCase(f.getName())) {
				roundFile = f;
			}
			else if (holeByHoleFileName.equalsIgnoreCase(f.getName())) {
				holeByHoleFile = f;
			}
		}

		assertNotNull(roundFile);
		assertNotNull(holeByHoleFile);

		List<GolfRound> rounds = new HoleByHoleRoundCSVParser(roundFile, holeByHoleFile).parse();
		assertNotNull(rounds);
		assertFalse(rounds.isEmpty());
		assertEquals(1, rounds.size());

		GolfRound round = rounds.get(0);
		assertEquals(83, round.getStrokes());
		assertEquals(31, round.getPutts());
		assertEquals(30, Game.stablefordAllPositive(round).getScore());
	}

	@Test
	void testMultiRoundFiles() {
		String roundFileName = "testHoleByHoleMultiRound.csv";
		String holeByHoleFileName = "testHoleByHoleMultiRoundDetail.csv";

		File roundFile = null, holeByHoleFile = null;

		File directory = new File(System.getProperty("user.dir"), "src/test/resources/holeByHoleCSVParser");
		for (File f : Objects.requireNonNull(directory.listFiles())) {
			if (roundFileName.equalsIgnoreCase(f.getName())) {
				roundFile = f;
			}
			else if (holeByHoleFileName.equalsIgnoreCase(f.getName())) {
				holeByHoleFile = f;
			}
		}

		assertNotNull(roundFile);
		assertNotNull(holeByHoleFile);

		List<GolfRound> rounds = new HoleByHoleRoundCSVParser(roundFile, holeByHoleFile).parse();
		assertNotNull(rounds);
		assertFalse(rounds.isEmpty());
		assertEquals(17, rounds.size());

		GolfRound firstRound = rounds.get(0);
		assertEquals(LocalDate.of(2018, 6, 17), firstRound.getDate());
		assertEquals(new BigDecimal("70.7").divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP), firstRound.getRating());
		assertEquals(new BigDecimal("131"), firstRound.getSlope());
		assertEquals(9, firstRound.getHoleCount());
		assertEquals(36, firstRound.getPar());
		assertEquals(15, firstRound.getPutts());
		assertEquals(7, firstRound.getFairways());
		assertEquals(4, firstRound.getFairwaysInRegulation());
		assertEquals(0, firstRound.getGreensInRegulation());
	}
}
