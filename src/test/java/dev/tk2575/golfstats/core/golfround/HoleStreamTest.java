package dev.tk2575.golfstats.core.golfround;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HoleStreamTest {

	@Test
	void testShuffleIndexes() {
		var holeList = List.of(
				hole(1,9),
				hole(2, 1),
				hole(3, 8),
				hole(4,2),
				hole(5, 7),
				hole(6, 3),
				hole(7, 6),
				hole(8,4),
				hole(9, 5)
		);

		var expected = List.of(
				hole(1, 17),
				hole(2, 1),
				hole(3, 15),
				hole(4,3),
				hole(5, 13),
				hole(6, 5),
				hole(7, 11),
				hole(8,7),
				hole(9, 9)
		);

		assertEquals(expected, Hole.stream(holeList).shuffleNineHoleIndexesOdd().toList());

	}

	private Hole hole(int number, int index) {
		return Hole.of(number, index, 4, 4, true, 2);
	}

}