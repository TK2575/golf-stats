package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.games.Game;
import dev.tk2575.golfstats.details.CSVFile;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static dev.tk2575.Utils.readCSVFilesInDirectory;
import static org.junit.jupiter.api.Assertions.*;

class ShotByShotRoundCSVParserTest {

	@Test
	void testSingleRoundFiles() {
		List<CSVFile> files = readCSVFilesInDirectory("shotByShotCSVParser")
				.stream()
				.filter(each -> !each.getName().toLowerCase().contains("multi"))
				.toList();
		assertNotNull(files);
		assertEquals(2, files.size());

		List<GolfRound> rounds = new ShotByShotRoundCSVParser(files).parse();
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
		assertEquals(5078L, round.getYards());
	}

	@Test
	void testMultiRoundFiles() {
		List<CSVFile> files = readCSVFilesInDirectory("shotByShotCSVParser")
				.stream()
				.filter(each -> each.getName().toLowerCase().contains("multi"))
				.toList();
		assertNotNull(files);
		assertEquals(2, files.size());

		List<GolfRound> rounds = new ShotByShotRoundCSVParser(files).parse();
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

		assertEquals(round.getStrokesGained(), round.getHoles()
		                                            .totalStrokesGainedBaseline()
		                                            .subtract(BigDecimal.valueOf(round.getStrokes())));

		Map<String, BigDecimal> strokesGainedByShotType = round.getHoles().strokesGainedByShotType();
		assertEquals(new BigDecimal("-1.62"), strokesGainedByShotType.get("Tee"));
		assertEquals(new BigDecimal("-2.15"), strokesGainedByShotType.get("Approach"));
		assertEquals(new BigDecimal("-2.96"), strokesGainedByShotType.get("Around Green"));
		assertEquals(new BigDecimal("-5.96"), strokesGainedByShotType.get("Green"));

		assertEquals(round.getStrokesGained(), strokesGainedByShotType.values()
		                                                              .stream()
		                                                              .reduce(BigDecimal.ZERO, BigDecimal::add));
	}

}