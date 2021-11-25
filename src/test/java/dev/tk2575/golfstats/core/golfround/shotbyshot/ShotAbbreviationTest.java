package dev.tk2575.golfstats.core.golfround.shotbyshot;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class ShotAbbreviationTest {

	@Test
	void testValidate() {
		List<String> valid = List.of("T455", "T-432", "T455x3", "Y186", "Y186x2", "R122L", "R122-L", "F-6-12", "G27-4", "G13-7x31", "S18-4");
		List<String> invalid = List.of("A455", "T654321", "G9x", "R99-1234567", "F23-1x984", "32FR");

		valid.forEach(e -> assertTrue(String.format("%s should match pattern", e), ShotAbbreviation.validate(e)));
		invalid.forEach(e -> assertFalse(String.format("%s should not match pattern", e), ShotAbbreviation.validate(e)));
	}

	@Test
	void testShorthandEntry() {
		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.tee())
		                       .distanceFromTarget(Distance.yards(455))
		                       .priorShotMissAngle(MissAngle.center())
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("T455"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.tee())
		                       .distanceFromTarget(Distance.yards(455))
		                       .priorShotMissAngle(MissAngle.center())
		                       .count(3)
		                       .build(), ShotAbbreviation.parse("T455x3"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.recovery())
		                       .distanceFromTarget(Distance.yards(186))
		                       .priorShotMissAngle(MissAngle.center())
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("Y186"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.recovery())
		                       .distanceFromTarget(Distance.yards(186))
		                       .priorShotMissAngle(MissAngle.center())
		                       .count(2)
		                       .build(), ShotAbbreviation.parse("Y186x2"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.rough())
		                       .distanceFromTarget(Distance.yards(122))
		                       .priorShotMissAngle(MissAngle.missLeft())
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("R122L"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.rough())
		                       .distanceFromTarget(Distance.yards(122))
		                       .priorShotMissAngle(MissAngle.missLeft())
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("R122-L"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.fairway())
		                       .distanceFromTarget(Distance.yards(6))
		                       .priorShotMissAngle(MissAngle.missDegrees(12))
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("F6-12"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.green())
		                       .distanceFromTarget(Distance.feet(27))
		                       .priorShotMissAngle(MissAngle.missDegrees(4))
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("G27-4"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.green())
		                       .distanceFromTarget(Distance.feet(13))
		                       .priorShotMissAngle(MissAngle.missDegrees(7))
		                       .count(3)
		                       .build(), ShotAbbreviation.parse("G13-7x3"));

		assertEquals(ShotAbbreviation.builder()
		                       .lie(Lie.sand())
		                       .distanceFromTarget(Distance.yards(18))
		                       .priorShotMissAngle(MissAngle.missDegrees(4))
		                       .count(1)
		                       .build(), ShotAbbreviation.parse("S18-4"));
	}

	@Test
	void testShorthandIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("A455"));
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("T654321"));
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("G9x"));
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("R99-1234567"));
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("F23-1x984"));
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("32FR"));
		assertThrows(IllegalArgumentException.class, () -> ShotAbbreviation.parse("G27-13"));
	}

}