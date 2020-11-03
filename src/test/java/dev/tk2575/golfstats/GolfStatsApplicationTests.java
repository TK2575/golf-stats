package dev.tk2575.golfstats;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GolfStatsApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

	@Test
	void testGolfNameSubstring() {
		String example = "scores_will.csv";
		String expected = "will";
		String first = example.substring(example.lastIndexOf('_') + 1);
		String result = first.substring(0, first.lastIndexOf('.'));
		assertEquals(expected, result);

	}

	@Test
	void testDivisionToBigDecimal() {
		int holes = 18;
		int putts = 30;
		BigDecimal result = BigDecimal.valueOf((float) putts / holes).setScale(2, HALF_UP);
		assertEquals(BigDecimal.valueOf(1.67), result);
	}

	@Test
	void testCourseRatingCorrection() {
		BigDecimal rating = new BigDecimal("69.8");
		int par = 36;
		assertTrue(rating.subtract(BigDecimal.valueOf(par)).compareTo(BigDecimal.TEN) > 0);
		BigDecimal correctedRating = rating.divide(new BigDecimal("2"), 2, HALF_UP);
		assertEquals(new BigDecimal("34.90"), correctedRating);
	}

	@Test
	void testScoreDifferential() {
		BigDecimal effectiveCourseRating = new BigDecimal("71.90");
		BigDecimal slope = new BigDecimal("127.00");
		int score = 106;

		BigDecimal firstTerm = BigDecimal.valueOf(113).divide(slope, 2, HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(score).subtract(effectiveCourseRating);
		BigDecimal result = firstTerm.multiply(secondTerm).setScale(1, HALF_UP);

		assertEquals(new BigDecimal("30.3"), result);
	}

}
