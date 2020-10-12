package dev.tk2575.golfstats;

import dev.tk2575.golfstats.handicapindex.HandicapIndex;

public interface Golfer {

	static Golfer newGolfer(String golferName) {
		return new SimpleGolfer(golferName);
	}

	static Golfer of(String golferName, HandicapIndex index) { return new SimpleGolfer(golferName, index); }

	String getName();

	HandicapIndex getHandicapIndex();

	String getGender();
}
