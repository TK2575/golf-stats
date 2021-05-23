package dev.tk2575.golfstats.core.golfer;

import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
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
		this(golferName, index, UNKNOWN);
	}

	public SimpleGolfer(Golfer golfer, HandicapIndex index) {
		this(golfer.getName(), index, golfer.getGender());
	}

	public SimpleGolfer(String name, HandicapIndex index, String gender) {
		this.name = name;
		this.handicapIndex = index == null ? HandicapIndex.emptyIndex() : index;
		this.gender = gender;
	}
}
