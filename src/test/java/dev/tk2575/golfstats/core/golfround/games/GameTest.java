package dev.tk2575.golfstats.core.golfround.games;

import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.SimpleHoleScore;
import org.junit.jupiter.api.Test;

import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

	@Test
	void testStablefordPoints() {
		SimpleHoleScore hole = SimpleHoleScore.builder().number(1).index(18).fairwayInRegulation(true).putts(2).build();
		final ToIntFunction<Hole> stableford = StablefordAllPositive.rules;

		assertEquals(0, stableford.applyAsInt(hole.toBuilder().par(3).strokes(5).build()));
		assertEquals(1, stableford.applyAsInt(hole.toBuilder().par(3).strokes(4).build()));
		assertEquals(2, stableford.applyAsInt(hole.toBuilder().par(3).strokes(3).build()));
		assertEquals(4, stableford.applyAsInt(hole.toBuilder().par(3).strokes(2).build()));
		assertEquals(8, stableford.applyAsInt(hole.toBuilder().par(3).strokes(1).build()));

		assertEquals(0, stableford.applyAsInt(hole.toBuilder().par(4).strokes(6).build()));
		assertEquals(1, stableford.applyAsInt(hole.toBuilder().par(4).strokes(5).build()));
		assertEquals(2, stableford.applyAsInt(hole.toBuilder().par(4).strokes(4).build()));
		assertEquals(4, stableford.applyAsInt(hole.toBuilder().par(4).strokes(3).build()));
		assertEquals(8, stableford.applyAsInt(hole.toBuilder().par(4).strokes(2).build()));

		assertEquals(0, stableford.applyAsInt(hole.toBuilder().par(5).strokes(7).build()));
		assertEquals(1, stableford.applyAsInt(hole.toBuilder().par(5).strokes(6).build()));
		assertEquals(2, stableford.applyAsInt(hole.toBuilder().par(5).strokes(5).build()));
		assertEquals(4, stableford.applyAsInt(hole.toBuilder().par(5).strokes(4).build()));
		assertEquals(8, stableford.applyAsInt(hole.toBuilder().par(5).strokes(3).build()));
	}

}