package dev.tk2575.golfstats.core.golfround.games;

import dev.tk2575.golfstats.core.golfround.Hole;
import org.junit.jupiter.api.Test;

import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

	private Hole hole(int par, int strokes) {
		return Hole.of(1, 18, par, strokes, true, 2);
	}

	@Test
	void testStablefordPoints() {
		final ToIntFunction<Hole> stableford = StablefordAllPositive.rules;

		assertEquals(0, stableford.applyAsInt(hole(3,5)));
		assertEquals(1, stableford.applyAsInt(hole(3,4)));
		assertEquals(2, stableford.applyAsInt(hole(3,3)));
		assertEquals(4, stableford.applyAsInt(hole(3,2)));
		assertEquals(8, stableford.applyAsInt(hole(3,1)));

		assertEquals(0, stableford.applyAsInt(hole(4,6)));
		assertEquals(1, stableford.applyAsInt(hole(4,5)));
		assertEquals(2, stableford.applyAsInt(hole(4,4)));
		assertEquals(4, stableford.applyAsInt(hole(4,3)));
		assertEquals(8, stableford.applyAsInt(hole(4,2)));

		assertEquals(0, stableford.applyAsInt(hole(5,7)));
		assertEquals(1, stableford.applyAsInt(hole(5,6)));
		assertEquals(2, stableford.applyAsInt(hole(5,5)));
		assertEquals(4, stableford.applyAsInt(hole(5,4)));
		assertEquals(8, stableford.applyAsInt(hole(5,3)));
	}

}