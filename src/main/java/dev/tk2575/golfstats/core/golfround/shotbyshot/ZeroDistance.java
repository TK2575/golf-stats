package dev.tk2575.golfstats.core.golfround.shotbyshot;

public class ZeroDistance implements Distance {

	@Override
	public Long getValue() {
		return 0L;
	}

	@Override
	public String getLengthUnit() {
		return "";
	}

	@Override
	public Long getLengthInYards() {
		return getValue();
	}

	@Override
	public Long getLengthInFeet() {
		return getValue();
	}

	@Override
	public Distance add(Distance distance) {
		return this;
	}

	@Override
	public Distance subtract(Distance distance) {
		return this;
	}

	@Override
	public Distance convertToSameUnit(Distance distance) {
		return this;
	}

	@Override
	public Distance ofSameUnit(long value) {
		return this;
	}

	@Override
	public boolean isLessThanOrEqualToYards(int yards) {
		return true;
	}
}
