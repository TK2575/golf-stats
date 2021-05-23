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
}
