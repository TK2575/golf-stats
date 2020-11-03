package dev.tk2575.golfstats.golfround.holebyhole;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HoleTest {

	@Test
	void testGreenInRegulation() {
		SimpleHoleScore hole = SimpleHoleScore.builder()
		                                      .number(1)
		                                      .index(18)
		                                      .fairwayInRegulation(true)
		                                      .build();

		assertTrue(hole.toBuilder().par(4).strokes(3).putts(1).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(4).strokes(3).putts(0).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(4).strokes(4).putts(2).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(4).strokes(4).putts(1).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(4).strokes(5).putts(3).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(4).strokes(5).putts(2).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(4).strokes(6).putts(4).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(4).strokes(6).putts(2).build().isGreenInRegulation());

		assertTrue(hole.toBuilder().par(3).strokes(2).putts(1).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(3).strokes(2).putts(0).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(3).strokes(3).putts(2).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(3).strokes(3).putts(1).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(3).strokes(4).putts(3).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(3).strokes(4).putts(2).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(3).strokes(5).putts(4).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(3).strokes(5).putts(2).build().isGreenInRegulation());

		assertTrue(hole.toBuilder().par(5).strokes(4).putts(1).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(5).strokes(4).putts(0).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(5).strokes(5).putts(2).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(5).strokes(5).putts(1).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(5).strokes(6).putts(3).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(5).strokes(6).putts(2).build().isGreenInRegulation());
		assertTrue(hole.toBuilder().par(5).strokes(7).putts(4).build().isGreenInRegulation());
		assertFalse(hole.toBuilder().par(5).strokes(7).putts(2).build().isGreenInRegulation());

	}

	@Test
	void testStablefordPoints() {
		SimpleHoleScore hole = SimpleHoleScore.builder()
		                                      .number(1)
		                                      .index(18)
		                                      .fairwayInRegulation(true)
		                                      .putts(2)
		                                      .build();

		assertEquals(0, hole.toBuilder().par(3).strokes(5).build().getStablefordPoints());
		assertEquals(1, hole.toBuilder().par(3).strokes(4).build().getStablefordPoints());
		assertEquals(2, hole.toBuilder().par(3).strokes(3).build().getStablefordPoints());
		assertEquals(4, hole.toBuilder().par(3).strokes(2).build().getStablefordPoints());
		assertEquals(8, hole.toBuilder().par(3).strokes(1).build().getStablefordPoints());

		assertEquals(0, hole.toBuilder().par(4).strokes(6).build().getStablefordPoints());
		assertEquals(1, hole.toBuilder().par(4).strokes(5).build().getStablefordPoints());
		assertEquals(2, hole.toBuilder().par(4).strokes(4).build().getStablefordPoints());
		assertEquals(4, hole.toBuilder().par(4).strokes(3).build().getStablefordPoints());

		assertEquals(0, hole.toBuilder().par(5).strokes(7).build().getStablefordPoints());
		assertEquals(1, hole.toBuilder().par(5).strokes(6).build().getStablefordPoints());
		assertEquals(2, hole.toBuilder().par(5).strokes(5).build().getStablefordPoints());
		assertEquals(4, hole.toBuilder().par(5).strokes(4).build().getStablefordPoints());
		assertEquals(8, hole.toBuilder().par(5).strokes(3).build().getStablefordPoints());
	}

}