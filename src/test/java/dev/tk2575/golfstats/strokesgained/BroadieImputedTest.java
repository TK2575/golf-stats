package dev.tk2575.golfstats.strokesgained;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BroadieImputedTest {

	@Test
	void testAllDistancesPresent() {
		BroadieImputed svc = BroadieImputed.getInstance();
		for (int i = 1; i <= 600; i++) {
			assertNotNull(svc.getStrokesGainedMap().get("t"+i));
			assertNotNull(svc.getStrokesGainedMap().get("f"+i));
			assertNotNull(svc.getStrokesGainedMap().get("r"+i));
			assertNotNull(svc.getStrokesGainedMap().get("s"+i));
			assertNotNull(svc.getStrokesGainedMap().get("y"+i));
			if (i <= 90) { assertNotNull(svc.getStrokesGainedMap().get("g"+i)); }
		}
	}

	@Test
	void testBaselineResults() {
		BroadieImputed svc = BroadieImputed.getInstance();
		assertEquals(new BigDecimal("3.79"), svc.getStrokesGainedMap().get("t320"));
		assertEquals(new BigDecimal("3.86"), svc.getStrokesGainedMap().get("t340"));
		assertEquals(new BigDecimal("3.83"), svc.getStrokesGainedMap().get("t330"));

		assertEquals(new BigDecimal("2.85"), svc.getStrokesGainedMap().get("f120"));
		assertEquals(new BigDecimal("2.91"), svc.getStrokesGainedMap().get("f140"));
		assertEquals(new BigDecimal("2.87"), svc.getStrokesGainedMap().get("f125"));

		assertEquals(new BigDecimal("3.23"), svc.getStrokesGainedMap().get("r160"));
		assertEquals(new BigDecimal("3.31"), svc.getStrokesGainedMap().get("r180"));
		assertEquals(new BigDecimal("3.29"), svc.getStrokesGainedMap().get("r175"));

		assertEquals(new BigDecimal("2.53"), svc.getStrokesGainedMap().get("s20"));
		assertEquals(new BigDecimal("2.82"), svc.getStrokesGainedMap().get("s40"));
		assertEquals(new BigDecimal("2.63"), svc.getStrokesGainedMap().get("s27"));

		assertEquals(new BigDecimal("3.97"), svc.getStrokesGainedMap().get("y240"));
		assertEquals(new BigDecimal("4.03"), svc.getStrokesGainedMap().get("y260"));
		assertEquals(new BigDecimal("4.01"), svc.getStrokesGainedMap().get("y253"));

		assertEquals(new BigDecimal("1.61"), svc.getStrokesGainedMap().get("g10"));
		assertEquals(new BigDecimal("1.78"), svc.getStrokesGainedMap().get("g15"));
		assertEquals(new BigDecimal("1.71"), svc.getStrokesGainedMap().get("g13"));
	}

}