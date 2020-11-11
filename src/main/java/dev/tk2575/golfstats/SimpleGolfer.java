package dev.tk2575.golfstats;

import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
public class SimpleGolfer implements Golfer {

	private final String name;
	@EqualsAndHashCode.Exclude private final HandicapIndex handicapIndex;
	private final String gender;

	public SimpleGolfer(String golferName) {
		this(golferName, null);
	}

	public SimpleGolfer(String golferName, HandicapIndex index) {
		this.name = golferName;
		this.handicapIndex = assignIndex(index);
		this.gender = UNKNOWN;
	}

	public SimpleGolfer(Golfer golfer, HandicapIndex index) {
		this.name = golfer.getName();
		this.handicapIndex = assignIndex(index);
		this.gender = golfer.getGender();
	}

	private HandicapIndex assignIndex(HandicapIndex index) {
		return index == null ? HandicapIndex.emptyIndex() : index;
	}
}
