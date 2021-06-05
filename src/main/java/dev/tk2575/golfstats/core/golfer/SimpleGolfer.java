package dev.tk2575.golfstats.core.golfer;

import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
class SimpleGolfer implements Golfer {

	private final String name;
	private final String gender;

	@EqualsAndHashCode.Exclude
	private final HandicapIndex handicapIndex;

	SimpleGolfer(String golferName) {
		this(golferName, null);
	}

	SimpleGolfer(String golferName, HandicapIndex index) {
		this(golferName, index, UNKNOWN);
	}

	SimpleGolfer(Golfer golfer, HandicapIndex index) {
		this(golfer.getName(), index, golfer.getGender());
	}

	SimpleGolfer(String name, HandicapIndex index, String gender) {
		this.name = name;
		this.handicapIndex = index == null ? HandicapIndex.emptyIndex() : index;
		this.gender = gender;
	}
}
