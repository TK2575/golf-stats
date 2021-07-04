package dev.tk2575.golfstats.core.golfround;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GolfRoundStreamTest {

	@Test
	void testEmpty() {
		assertEquals("", GolfRoundStream.empty().golferNames());
		assertEquals(BigDecimal.ZERO, GolfRoundStream.empty().meanDifferential());
		assertTrue(GolfRoundStream.empty().compileTo18HoleRounds().isEmpty());
		assertTrue(GolfRoundStream.empty().sortOldestToNewest().isEmpty());
		assertTrue(GolfRoundStream.empty().sortNewestToOldest().isEmpty());
		assertTrue(GolfRoundStream.empty().newestRound().isEmpty());
		assertTrue(GolfRoundStream.empty().oldestRound().isEmpty());
		assertTrue(GolfRoundStream.empty().lowestDifferentials().isEmpty());
		assertTrue(GolfRoundStream.empty().highestDifferentials().isEmpty());
		assertEquals(BigDecimal.ZERO, GolfRoundStream.empty().fairwaysInRegulation());
		assertEquals(BigDecimal.ZERO, GolfRoundStream.empty().greensInRegulation());
		assertEquals(BigDecimal.ZERO, GolfRoundStream.empty().puttsPerHole());
		assertEquals(0L, GolfRoundStream.empty().minutesPerRound());
		assertEquals(BigDecimal.ZERO, GolfRoundStream.empty().medianDifferential());
		assertTrue(GolfRoundStream.empty().lowestDifferential().isEmpty());
		assertTrue(GolfRoundStream.empty().largestDifferential().isEmpty());
		assertTrue(GolfRoundStream.empty().lowestScoreRound().isEmpty());
		assertTrue(GolfRoundStream.empty().highestScoreRound().isEmpty());
	}

}