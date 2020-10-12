package dev.tk2575.golfstats;

import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
public class SimpleGolfer implements Golfer {

	private static final String UNKNOWN = "UNKNOWN";

	private final String name;
	@EqualsAndHashCode.Exclude private final HandicapIndex handicapIndex;
	private final String gender;

	public SimpleGolfer(String golferName) {
		this.name = golferName;
		this.handicapIndex = HandicapIndex.emptyIndex();
		this.gender = UNKNOWN;
	}

	public SimpleGolfer(String golferName, HandicapIndex index) {
		this.name = golferName;
		this.handicapIndex = index;
		this.gender = UNKNOWN;
	}
}
