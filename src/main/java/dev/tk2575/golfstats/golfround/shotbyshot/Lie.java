package dev.tk2575.golfstats.golfround.shotbyshot;

import java.util.Set;

public interface Lie {

	Set<Character> SUPPORTED_LIES = Set.of('t','f','y','r','g','s');

	static Lie parse(char abbrev) {
		if (Character.isAlphabetic(abbrev)) {
			char abbrevLower = Character.toLowerCase(abbrev);

			if (SUPPORTED_LIES.contains(abbrevLower)) {
				Lie result = switch(abbrevLower) {
					case 't' -> tee();
					case 'f' -> fairway();
					case 'y' -> recovery();
					case 'r' -> rough();
					case 'g' -> green();
					case 's' -> sand();
					default -> null;
				};
				if (result != null) return result;
			}
		}
		throw new IllegalArgumentException(abbrev + " is not a supported abbrev");
	}

	String getLabel();

	String getAbbrev();

	static Lie tee() {
		return new TeeLie();
	}

	static Lie fairway() {
		return new FairwayLie();
	}

	static Lie recovery() {
		return new RecoveryLie();
	}

	static Lie rough() {
		return new RoughLie();
	}

	static Lie green() {
		return new GreenLie();
	}

	static Lie sand() {
		return new SandLie();
	}

	static Lie hole() {
		return new HoleLie();
	}

	default boolean isGreen() {
		return false;
	}

	default boolean isFairway() { return false; }

	default boolean isTee() { return false; }
}
