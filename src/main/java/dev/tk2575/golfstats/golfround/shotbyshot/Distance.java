package dev.tk2575.golfstats.golfround.shotbyshot;

public interface Distance {

	static Distance parse(Lie lie, long value) {
		if (lie.isGreen()) {
			return feet(value);
		}
		else return yards(value);
	}

	Long getValue();

	String getLengthUnit();

	static Distance feet(long value) {
		return new FeetDistance(value);
	}

	static Distance yards(long value) {
		return new YardsDistance(value);
	}
}
