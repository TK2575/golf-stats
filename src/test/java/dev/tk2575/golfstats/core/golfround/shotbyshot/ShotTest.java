package dev.tk2575.golfstats.core.golfround.shotbyshot;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class ShotTest {

	@Test
	void testValidate() {
		List<String> valid = List.of("T455", "T-432", "T455x3", "Y186", "Y186x2", "R122L", "R122-L", "F-6-12", "G27-4", "G13-7x31", "S18-4");
		List<String> invalid = List.of("A455", "T654321", "G9x", "R99-1234567", "F23-1x984", "32FR");

		valid.forEach(e -> assertTrue(String.format("%s should match pattern", e), Shot.validate(e)));
		invalid.forEach(e -> assertFalse(String.format("%s should not match pattern", e), Shot.validate(e)));
	}

	@Test
	void testShorthandEntry() {
		assertEquals(SimpleShot.builder()
		                       .lie(Lie.tee())
		                       .distance(Distance.yards(455))
		                       .missAngle(MissAngle.center())
		                       .count(1)
		                       .build(), Shot.parse("T455"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.tee())
		                       .distance(Distance.yards(455))
		                       .missAngle(MissAngle.center())
		                       .count(3)
		                       .build(), Shot.parse("T455x3"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.recovery())
		                       .distance(Distance.yards(186))
		                       .missAngle(MissAngle.center())
		                       .count(1)
		                       .build(), Shot.parse("Y186"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.recovery())
		                       .distance(Distance.yards(186))
		                       .missAngle(MissAngle.center())
		                       .count(2)
		                       .build(), Shot.parse("Y186x2"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.rough())
		                       .distance(Distance.yards(122))
		                       .missAngle(MissAngle.missLeft())
		                       .count(1)
		                       .build(), Shot.parse("R122L"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.rough())
		                       .distance(Distance.yards(122))
		                       .missAngle(MissAngle.missLeft())
		                       .count(1)
		                       .build(), Shot.parse("R122-L"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.fairway())
		                       .distance(Distance.yards(6))
		                       .missAngle(MissAngle.missDegrees(12))
		                       .count(1)
		                       .build(), Shot.parse("F6-12"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.green())
		                       .distance(Distance.feet(27))
		                       .missAngle(MissAngle.missDegrees(4))
		                       .count(1)
		                       .build(), Shot.parse("G27-4"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.green())
		                       .distance(Distance.feet(13))
		                       .missAngle(MissAngle.missDegrees(7))
		                       .count(3)
		                       .build(), Shot.parse("G13-7x3"));

		assertEquals(SimpleShot.builder()
		                       .lie(Lie.sand())
		                       .distance(Distance.yards(18))
		                       .missAngle(MissAngle.missDegrees(4))
		                       .count(1)
		                       .build(), Shot.parse("S18-4"));
	}

	@Test
	void testShorthandIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("A455"));
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("T654321"));
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("G9x"));
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("R99-1234567"));
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("F23-1x984"));
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("32FR"));
		assertThrows(IllegalArgumentException.class, () -> Shot.parse("G27-13"));
	}

}