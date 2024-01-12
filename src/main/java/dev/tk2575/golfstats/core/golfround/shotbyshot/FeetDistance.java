package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FeetDistance implements Distance {

	private final Long value;

	@Override
	public String getLengthUnit() {
		return "feet";
	}

	@Override
	public Long getLengthInYards() {
		return value / 3;
	}

	@Override
	public Long getLengthInFeet() {
		return getValue();
	}
}
