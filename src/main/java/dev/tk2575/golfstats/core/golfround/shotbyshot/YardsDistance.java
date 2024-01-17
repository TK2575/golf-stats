package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class YardsDistance implements Distance {

	private final Long value;

	@Override
	public String getLengthUnit() {
		return "yards";
	}

	@Override
	public Long getLengthInYards() {
		return getValue();
	}

	@Override
	public Long getLengthInFeet() {
		return getValue() * 3;
	}
}
