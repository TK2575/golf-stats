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

	@Override
	public Distance add(Distance distance) {
		return Distance.feet(this.getValue() + distance.getLengthInFeet());
	}

	@Override
	public Distance subtract(Distance distance) {
		return Distance.feet(this.getValue() - distance.getLengthInFeet());
	}

	@Override
	public Distance convertToSameUnit(Distance distance) {
		return Distance.feet(distance.getLengthInFeet());
	}

	@Override
	public Distance ofSameUnit(long value) {
		return Distance.feet(value);
	}

	@Override
	public boolean isLessThanOrEqualToYards(int yards) {
		return getLengthInYards() >= yards;
	}
}
