package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.games.Game;
import dev.tk2575.golfstats.details.CSVFile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleGolfRoundCSVParserTest {

	@Test
	void testMultiRoundFile() {
		CSVFile csvFile = new CSVFile("simple_rounds.csv", """
				date,course,tees,rating,slope,par,duration,transport,score,fairways_hit,fairways,greens_in_reg,putts,nine_hole_round,golfer
				5/17/2014,Indian Spring CC,White,34.9,118,35,2:53,Cart,44,5,7,2,18,TRUE,Tom
				5/23/2014,Ramblewood CC (White/Blue),White,35.2,121,36,1:48,Cart,44,4,7,4,18,TRUE,Tom
				5/24/2014,Golden Pheasant GC,White,67.0,116,72,4:22,Cart,99,6,14,1,35,FALSE,Tom
				5/26/2014,Ramblewood CC (Blue/Red),White,34.6,127,36,1:04,Cart,48,5,7,2,20,TRUE,Tom
				5/29/2014,Tavistock CC,White,67.5,122,72,4:54,Cart,101,6,14,2,39,FALSE,Tom
				5/31/2014,Ramblewood CC (Blue/Red),White,69.8,126,72,3:59,Cart,88,12,14,5,31,FALSE,Tom
				6/29/2014,Golden Pheasant GC,White,67.0,116,72,4:33,Cart,101,6,14,2,34,FALSE,Tom
				7/6/2014,Rancocas GC,White,68.7,126,71,4:31,Cart,96,6,14,2,34,FALSE,Tom
				8/11/2014,McCulloughs Emerald GL,White,67.8,119,71,,Cart,95,5,13,3,36,FALSE,Tom
				8/15/2014,Sand Barrens Golf Club (South/North),,34.9,125,36,,Cart,45,7,7,2,18,TRUE,Tom
				""");

		List<GolfRound> rounds = new SimpleGolfRoundCSVParser(List.of(csvFile)).parse();
		assertNotNull(rounds);
		assertFalse(rounds.isEmpty());
		assertEquals(10, rounds.size());

		GolfRound round = rounds.get(2);
		assertFalse(round.isNineHoleRound());
		assertEquals(18, round.getHoleCount());
		assertEquals(99, round.getStrokes());
		assertEquals(35, round.getPutts());
		assertEquals(6, round.getFairwaysInRegulation());
		assertEquals(1, round.getGreensInRegulation());
	}

}