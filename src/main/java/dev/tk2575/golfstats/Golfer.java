package dev.tk2575.golfstats;

import dev.tk2575.golfstats.handicapindex.HandicapIndex;

public interface Golfer {

	String UNKNOWN = "UNKNOWN";

	static Golfer newGolfer(String golferName) {
		return new SimpleGolfer(golferName);
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
