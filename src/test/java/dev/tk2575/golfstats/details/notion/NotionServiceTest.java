package dev.tk2575.golfstats.details.notion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotionServiceTest {

	@Test
	public void testGetUUIDFromUrl() {
		var input = "https://www.notion.so/tk2575/Golf-Performance-Dashboard-d26813b2e6f74c6f85a022ddb2853d8d?pvs=4";
		var expected = "d26813b2-e6f7-4c6f-85a0-22ddb2853d8d";
		assertEquals(expected, NotionService.getUUIDFromUrl(input));

		input = "https://www.notion.so/tk2575/ee0e935d0b7d4a268974508179012383?v=25203d645af84a09bacdc3e4df8965ad";
		expected = "ee0e935d-0b7d-4a26-8974-508179012383";
		assertEquals(expected, NotionService.getUUIDFromUrl(input));
	}

}