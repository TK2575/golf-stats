package dev.tk2575.golfstats.golfround.holebyhole;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HoleTest {

	@Test
	void testGreenInRegulation() {
		SimpleHoleScore hole = SimpleHoleScore.builder().number(1).index(18).fairwayInRegulation(true).build();

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
	void testApplyNetDoubleBogey() {
		SimpleHoleScore hole = SimpleHoleScore.builder().par(4).index(18).strokes(4).build();
		Hole holeAdj = hole.applyNetDoubleBogey(17);
		assertEquals(4, holeAdj.getStrokesAdjusted());

		hole = hole.toBuilder().index(18).strokes(6).build();
		holeAdj = hole.applyNetDoubleBogey(17);
		assertEquals(6, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = hole.applyNetDoubleBogey(-2);
		assertEquals(5, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = hole.toBuilder().index(18).strokes(7).build().applyNetDoubleBogey(17);
		assertEquals(6, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = hole.toBuilder().index(1).strokes(6).build().applyNetDoubleBogey(19);
		assertEquals(6, holeAdj.getStrokesAdjusted());
		assertEquals(4, holeAdj.getNetStrokes());

		holeAdj = hole.toBuilder().index(1).strokes(8).build().applyNetDoubleBogey(19);
		assertEquals(8, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = hole.toBuilder().index(1).strokes(10).build().applyNetDoubleBogey(19);
		assertEquals(8, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = hole.toBuilder().index(1).strokes(4).build().applyNetDoubleBogey(3);
		assertEquals(4, holeAdj.getStrokesAdjusted());
		assertEquals(3, holeAdj.getNetStrokes());
	}

}
