package dev.tk2575.golfstats.details.imports;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.games.Game;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HoleByHoleRoundCSVParserTest {

	@Test
	void testSingleRoundFiles() {
		List<CSVFile> files = CSVFile.readCSVFilesInDirectory("holeByHoleCSVParser")
				.stream()
				.filter(each -> !each.getName().toLowerCase().contains("multi"))
				.toList();
		assertNotNull(files);
		assertEquals(2, files.size());

		List<GolfRound> rounds = new HoleByHoleRoundCSVParser(files).parse();
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
		List<CSVFile> files = CSVFile.readCSVFilesInDirectory("holeByHoleCSVParser")
				.stream()
				.filter(each -> each.getName().toLowerCase().contains("multi"))
				.toList();
		assertNotNull(files);
		assertEquals(2, files.size());

		List<GolfRound> rounds = new HoleByHoleRoundCSVParser(files).parse();
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
