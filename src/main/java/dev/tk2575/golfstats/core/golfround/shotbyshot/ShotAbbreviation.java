package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

import java.util.regex.Pattern;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ShotAbbreviation {

	private final Lie lie;
	private final Integer sequence;
	private final Distance distanceFromTarget;
	private final MissAngle priorShotMissAngle;
	private final Integer count;

	public static ShotAbbreviation of(char lieChar, int sequence, long distance, char missDirection, int missAngle, int count) {
		Lie lie = Lie.parse(lieChar);
		return new ShotAbbreviation(lie, sequence, Distance.parse(lie, distance), MissAngle.parse(missDirection, missAngle), count);
	}

	public static ShotAbbreviation parse(String shorthand, int sequence) {
		if (!validate(shorthand)) {
			throw new IllegalArgumentException(shorthand + " does not match expected shorthand pattern");
		}

		//get lie from first char, init other vars
		char lie = shorthand.charAt(0);
		long distance;
		char missDirection = 'c';
		int missAngle = -1;
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
			if (temp.equalsIgnoreCase("l") || temp.equalsIgnoreCase("r")) {
				missDirection = Character.toLowerCase(temp.charAt(0));
			}
			else {
				missAngle = Integer.parseInt(temp);
				missDirection = 'x';
			}
		}

		return of(lie, sequence, distance, missDirection, missAngle, count);
	}

	protected static boolean validate(String shorthand) {
		if (shorthand == null || shorthand.isBlank()) {
			throw new IllegalArgumentException("shorthand cannot be null or empty");
		}

		Pattern shorthandPattern = Pattern.compile("[tfyrgs](-)?[0-9]{1,3}(-?[lr])?(-[0-9]{1,2})?(x[0-9]{1,2})?");
		return shorthandPattern.matcher(shorthand.toLowerCase()).matches();
	}
}
