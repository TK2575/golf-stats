package dev.tk2575.golfstats.core.golfround.holebyhole;

import dev.tk2575.golfstats.core.golfround.Hole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HoleTest {

	private Hole hole(int par, int strokes, int putts) {
		return Hole.of(1, 18, par, strokes, true, putts);
	}

	private Hole holeByIndex(int index, int strokes) {
		return Hole.of(1, index, 4, strokes, true, 2);
	}

	@Test
	void testGreenInRegulation() {
		assertTrue(hole(4,3,1).isGreenInRegulation());
		assertFalse(hole(4, 3, 0).isGreenInRegulation());
		assertFalse(hole(4,3,0).isGreenInRegulation());
		assertTrue(hole(4,4,2).isGreenInRegulation());
		assertFalse(hole(4,4,1).isGreenInRegulation());
		assertTrue(hole(4,5,3).isGreenInRegulation());
		assertFalse(hole(4,5,2).isGreenInRegulation());
		assertTrue(hole(4,6,4).isGreenInRegulation());
		assertFalse(hole(4,6,2).isGreenInRegulation());

		assertTrue(hole(3,2,1).isGreenInRegulation());
		assertFalse(hole(3,2,0).isGreenInRegulation());
		assertTrue(hole(3,3,2).isGreenInRegulation());
		assertFalse(hole(3,3,1).isGreenInRegulation());
		assertTrue(hole(3,4,3).isGreenInRegulation());
		assertFalse(hole(3,4,2).isGreenInRegulation());
		assertTrue(hole(3,5,4).isGreenInRegulation());
		assertFalse(hole(3,5,2).isGreenInRegulation());

		assertTrue(hole(5,4,1).isGreenInRegulation());
		assertFalse(hole(5,4,0).isGreenInRegulation());
		assertTrue(hole(5,5,2).isGreenInRegulation());
		assertFalse(hole(5,5,1).isGreenInRegulation());
		assertTrue(hole(5,6,3).isGreenInRegulation());
		assertFalse(hole(5,6,2).isGreenInRegulation());
		assertTrue(hole(5,7,4).isGreenInRegulation());
		assertFalse(hole(5,7,2).isGreenInRegulation());

	}

	@Test
	void testApplyNetDoubleBogey() {
		Hole hole = holeByIndex(18, 4);
		Hole holeAdj = hole.applyNetDoubleBogey(17);
		assertEquals(4, holeAdj.getStrokesAdjusted());

		hole = holeByIndex(18, 6);
		holeAdj = hole.applyNetDoubleBogey(17);
		assertEquals(6, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = hole.applyNetDoubleBogey(-2);
		assertEquals(5, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = holeByIndex(18, 7).applyNetDoubleBogey(17);
		assertEquals(6, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = holeByIndex(1, 6).applyNetDoubleBogey(19);
		assertEquals(6, holeAdj.getStrokesAdjusted());
		assertEquals(4, holeAdj.getNetStrokes());

		holeAdj = holeByIndex(1, 8).applyNetDoubleBogey(19);
		assertEquals(8, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = holeByIndex(1, 10).applyNetDoubleBogey(19);
		assertEquals(8, holeAdj.getStrokesAdjusted());
		assertEquals(6, holeAdj.getNetStrokes());

		holeAdj = holeByIndex(1, 4).applyNetDoubleBogey(3);
		assertEquals(4, holeAdj.getStrokesAdjusted());
		assertEquals(3, holeAdj.getNetStrokes());
	}

}
