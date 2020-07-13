package dev.tk2575.golfstats;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GolfStatsApplicationTests {

	@Test
	void contextLoads() {
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
		Integer holes = 18;
		Integer putts = 30;
		BigDecimal result = BigDecimal.valueOf((float) putts / holes).setScale(2, HALF_UP);
		assertEquals(BigDecimal.valueOf(1.67), result);
	}

	@Test
	void testCourseRatingCorrection() {
		BigDecimal rating = new BigDecimal("69.8");
		Integer par = 36;
		assertTrue(rating.subtract(BigDecimal.valueOf(par)).compareTo(BigDecimal.TEN) > 0);
		BigDecimal correctedRating = rating.divide(new BigDecimal("2")).setScale(2, HALF_UP);
		assertEquals(new BigDecimal("34.90"), correctedRating);
	}

	@Test
	void testScoreDifferential() {
		BigDecimal effectiveCourseRating = new BigDecimal("71.90").setScale(2);
		BigDecimal slope = new BigDecimal("127.00").setScale(2);
		Integer score = 106;

		BigDecimal firstTerm = BigDecimal.valueOf(113).setScale(2).divide(slope, HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(score).subtract(effectiveCourseRating).setScale(2);
		BigDecimal result = firstTerm.multiply(secondTerm).setScale(1, HALF_UP);

		assertEquals(new BigDecimal("30.3"), result);
	}

}
