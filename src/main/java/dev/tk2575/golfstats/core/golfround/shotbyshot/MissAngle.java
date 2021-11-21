package dev.tk2575.golfstats.core.golfround.shotbyshot;

import java.util.Set;

public interface MissAngle {

	Set<Character> SHORTHAND_DIRECTIONS = Set.of('c','l','r');

	static MissAngle parse(char missDirection, int clockFace) {
		if (Character.isAlphabetic(missDirection) && SHORTHAND_DIRECTIONS.contains(missDirection)) {
			return new MissDirection(missDirection);
		}
		return new MissClockFace(clockFace);
	}

	static MissAngle missLeft() {
		return new MissDirection('l');
	}

	static MissAngle missRight() {
		return new MissDirection('r');
	}

	static MissAngle center() {
		return new MissDirection('c');
	}

	static MissAngle missDegrees(int angleShorthand) {
		return new MissClockFace(angleShorthand);
	}

	int getAngleDegrees();

	boolean isLeftMiss();

	boolean isRightMiss();

	boolean isShortMiss();

	boolean isLongMiss();

	default String getDescription() {
		StringBuilder sb = new StringBuilder();

		if (isShortMiss()) sb.append("Short ");
		else if (isLongMiss()) sb.append("Long ");

		if (isLeftMiss()) sb.append("Left");
		else if (isRightMiss()) sb.append("Right");

		String result = sb.toString().trim();
		return result.isBlank() ? "Center" : result;
	}
}
