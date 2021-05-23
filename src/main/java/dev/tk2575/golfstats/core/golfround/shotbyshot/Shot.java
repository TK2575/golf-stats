package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.golfstats.core.golfround.holebyhole.Hole;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Pattern;

public interface Shot {

	static ShotStream stream(Collection<Shot> shots) { return new ShotStream(shots); }

	Lie getLie();

	Distance getDistance();

	MissAngle getMissAngle();

	Integer getCount();

	default ShotCategory getShotCategory() {
		return ShotCategory.unknown();
	}

	default BigDecimal getStrokesGainedBaseline() {
		return BigDecimal.ZERO;
	}

	default BigDecimal getStrokesGained() {
		return BigDecimal.ZERO;
	}

	static Shot of(char lieChar, long distance, char missDirection, int missAngle, int count) {
		Lie lie = Lie.parse(lieChar);
		return new SimpleShot(lie, Distance.parse(lie, distance), MissAngle.parse(missDirection, missAngle), count);
	}

	static Shot categorize(Hole hole, Shot shot) {
		return new CategorizedShot(hole, shot);
	}

	static Shot strokesGained(Shot shot, BigDecimal strokesGainedBaseline, BigDecimal strokesGained) {
		return new StrokesGainedShot(shot, strokesGainedBaseline, strokesGained);
	}

	static Shot holed() {
		return new HoledShot();
	}

	static boolean validate(String shorthand) {
		if (shorthand == null || shorthand.isBlank()) {
			throw new IllegalArgumentException("shorthand cannot be null or empty");
		}

		Pattern shorthandPattern = Pattern.compile("[tfyrgs](-)?[0-9]{1,3}(-?[lr])?(-[0-9]{1,2})?(x[0-9]{1,2})?");
		return shorthandPattern.matcher(shorthand.toLowerCase()).matches();
	}

	static Shot parse(String shorthand) {
		if (!validate(shorthand)) {
			throw new IllegalArgumentException(shorthand + " does not match expected shorthand pattern");
		}

		//get lie from first char, init other vars
		char lie = shorthand.charAt(0);
		long distance;
		char missDirection = 'c';
		int missAngle = 0;
		int count = 1;

		//get and remove count from end
		String[] split = shorthand.substring(1).split("x");
		if (split.length > 2) {
			throw new IllegalArgumentException("too many x's");
		}
		if (split.length == 2) {
			count = Integer.parseInt(split[1]);
		}

		//strip leading dash if present
		String temp = split[0];
		if (temp.charAt(0) == '-') {
			temp = temp.substring(1);
		}

		//get distance from beginning up to next dash or letter
		StringBuilder sb = new StringBuilder();
		int pointer = 0;

		for (char c : temp.toCharArray()) {
			if (!Character.isDigit(c)) break;
			sb.append(c);
			pointer++;
		}
		distance = Long.parseLong(sb.toString());
		temp = temp.substring(pointer);

		//miss angle/direction last
		if (!temp.isBlank()) {
			if (temp.charAt(0) == '-') {
				temp = temp.substring(1);
			}
			if (temp.toLowerCase().equalsIgnoreCase("l") || temp.toLowerCase().equalsIgnoreCase("r")) {
				missDirection = Character.toLowerCase(temp.charAt(0));
			}
			else {
				missAngle = Integer.parseInt(temp);
				missDirection = 'x';
			}
		}

		return of(lie, distance, missDirection, missAngle, count);
	}

	default String getShorthand() {
		return getLie().getAbbrev().toUpperCase() + getDistance().getValue() + (getCount() > 1 ? "x" + getCount() :  "");
	}
}
