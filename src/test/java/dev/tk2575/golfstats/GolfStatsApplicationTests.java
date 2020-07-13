package dev.tk2575.golfstats;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
