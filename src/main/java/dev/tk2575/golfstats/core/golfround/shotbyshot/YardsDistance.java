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

	@Override
	public Distance add(Distance distance) {
		return Distance.yards(getValue() + distance.getLengthInYards());
	}

	@Override
	public Distance subtract(Distance distance) {
		return Distance.yards(getValue() - distance.getLengthInYards());
	}

	@Override
	public Distance convertToSameUnit(Distance distance) {
		return Distance.yards(distance.getLengthInYards());
	}

	@Override
	public Distance ofSameUnit(long value) {
		return Distance.yards(value);
	}

	@Override
	public boolean isLessThanOrEqualToYards(int yards) {
		return yards >= getValue();
	}
}
