package dev.tk2575.golfstats.core.golfer;

import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;

public interface Golfer {

	String UNKNOWN = "UNKNOWN";

	static Golfer unknownGolfer() {
		return new SimpleGolfer(UNKNOWN);
	}
	
	static Golfer newGolfer(String golferName) {
		return new SimpleGolfer(golferName);
	}
	
	static Golfer newGolfer(String golferName, String gender) {
		return new SimpleGolfer(golferName, null, gender);
	}

	static Golfer of(String golferName, HandicapIndex index) { return new SimpleGolfer(golferName, index); }

	static Golfer of(Golfer golfer, HandicapIndex index) {
		 return new SimpleGolfer(golfer, index);
	}

	String getName();

	HandicapIndex getHandicapIndex();

	String getGender();

	default String getKey() {
		return String.join("-", getName(), getHandicapIndex().getValue().toPlainString());
	}

	default Long getId() {
		return 0L;
	}
}
