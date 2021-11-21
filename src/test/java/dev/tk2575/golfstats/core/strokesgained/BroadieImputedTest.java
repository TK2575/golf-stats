package dev.tk2575.golfstats.core.strokesgained;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Distance;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Lie;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotAbbreviation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BroadieImputedTest {

	@Test
	void testAllDistancesPresent() {
		BroadieImputed svc = BroadieImputed.getInstance();
		assertEquals(BigDecimal.ZERO, svc.getStrokesGainedMap().get("h0"));
		for (int i = 1; i <= 600; i++) {
			assertNotNull(svc.getStrokesGainedMap().get("t" + i));
			assertNotNull(svc.getStrokesGainedMap().get("f" + i));
			assertNotNull(svc.getStrokesGainedMap().get("r" + i));
			assertNotNull(svc.getStrokesGainedMap().get("s" + i));
			assertNotNull(svc.getStrokesGainedMap().get("y" + i));
			if (i <= 90) { assertNotNull(svc.getStrokesGainedMap().get("g" + i)); }
		}
	}

	@Test
	void testBaselines() {
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
		assertEquals(new BigDecimal("2.62"), svc.getStrokesGainedMap().get("s27"));

		assertEquals(new BigDecimal("3.97"), svc.getStrokesGainedMap().get("y240"));
		assertEquals(new BigDecimal("4.03"), svc.getStrokesGainedMap().get("y260"));
		assertEquals(new BigDecimal("4.01"), svc.getStrokesGainedMap().get("y253"));

		assertEquals(new BigDecimal("1.61"), svc.getStrokesGainedMap().get("g10"));
		assertEquals(new BigDecimal("1.78"), svc.getStrokesGainedMap().get("g15"));
		assertEquals(new BigDecimal("1.71"), svc.getStrokesGainedMap().get("g13"));

		assertEquals(new BigDecimal("2.34"), svc.getStrokesGainedMap().get("r10"));
		assertEquals(new BigDecimal("2.18"), svc.getStrokesGainedMap().get("f10"));
		assertEquals(new BigDecimal("2.43"), svc.getStrokesGainedMap().get("s10"));
		assertEquals(new BigDecimal("3.45"), svc.getStrokesGainedMap().get("y10"));

		assertEquals(new BigDecimal("2.05"), svc.getStrokesGainedMap().get("r8"));

	}

	@Test
	void testShotsGained() {
		ShotAbbreviation first = ShotAbbreviation.builder().lie(Lie.tee()).distanceFromTarget(Distance.yards(330)).count(1).build();
		ShotAbbreviation second = ShotAbbreviation.builder().lie(Lie.fairway()).distanceFromTarget(Distance.yards(64)).count(1).build();
		ShotAbbreviation third = ShotAbbreviation.builder().lie(Lie.green()).distanceFromTarget(Distance.feet(8)).count(2).build();

		BroadieImputed svc = BroadieImputed.getInstance();
		assertEquals(new BigDecimal("0.12"), svc.analyzeShot(Shot.of(first, second)).getStrokesGained());
		assertEquals(new BigDecimal("0.21"), svc.analyzeShot(Shot.of(second, third)).getStrokesGained());
		assertEquals(new BigDecimal("-0.50"), svc.analyzeShot(Shot.holed(third)).getStrokesGained());
	}

}