package dev.tk2575.golfstats.golfround.holebyhole;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HoleByHoleRoundCSVParserTest {

	@Test
	void test() {

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

		HoleByHoleRound round = new HoleByHoleRoundCSVParser(roundFile, holeByHoleFile).parse();
		assertEquals(83, round.getScore());
		assertEquals(31, round.getPutts());
	}
}