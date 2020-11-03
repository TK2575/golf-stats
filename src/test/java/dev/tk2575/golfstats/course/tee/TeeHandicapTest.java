package dev.tk2575.golfstats.course.tee;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeeHandicapTest {

	@Test
	void testSingleBogeyGolferOnLevelTee() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Tee tee = Tee.of("White", new BigDecimal("72"), new BigDecimal("113"), 72);
		Tee handicap = tee.handicapOf(golfer);

		assertEquals(18, handicap.getHandicapStrokes().get(golfer.getKey()));
	}

	@Test
	void testSingleGolferOnUnlevelTee() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("14.2")));
		Tee tee = Tee.of("Blue", new BigDecimal("71.2"), new BigDecimal("125"), 72);
		Tee handicap = tee.handicapOf(golfer);

		assertEquals(15, handicap.getHandicapStrokes().get(golfer.getKey()));
	}

	@Test
	void testGolfersOnLevelTee() {
		Golfer tom = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Golfer will = Golfer.of("Will", HandicapIndex.of(new BigDecimal("24.0")));
		Tee tee = Tee.of("White", new BigDecimal("72"), new BigDecimal("113"), 72);
		Tee handicap = tee.handicapOf(List.of(tom, will));

		assertEquals(18, handicap.getHandicapStrokes().get(tom.getKey()));
		assertEquals(24, handicap.getHandicapStrokes().get(will.getKey()));
	}

	@Test
	void testGolfersUnLevelTee() {
		Golfer tom = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("14.2")));
		Golfer will = Golfer.of("Will", HandicapIndex.of(new BigDecimal("25.9")));
		Tee tee = Tee.of("Blue", new BigDecimal("71.2"), new BigDecimal("125"), 72);
		Tee handicap = tee.handicapOf(List.of(tom, will));

		assertEquals(15, handicap.getHandicapStrokes().get(tom.getKey()));
		assertEquals(28, handicap.getHandicapStrokes().get(will.getKey()));
	}

	@Test
	void testSingleBogeyGolferOnNineHoleTee() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Tee tee = Tee.of("White", new BigDecimal("30.7"), new BigDecimal("111"), 32);
		Tee handicap = tee.handicapOf(golfer);

		assertEquals(8, handicap.getHandicapStrokes().get(golfer.getKey()));
	}

}