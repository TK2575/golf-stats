package dev.tk2575.golfstats.golfround.shotbyshot;

public interface Distance {

	static Distance parse(Lie lie, long value) {
		if (lie.isGreen()) {
			return feet(value);
		}
		else { return yards(value); }
	}

	Long getValue();

	String getLengthUnit();

	static Distance feet(long value) {
		return new FeetDistance(value);
	}

	static Distance yards(long value) {
		return new YardsDistance(value);
	}

	static Distance zero() {
		return new ZeroDistance();
	}

	default boolean isLessThanOrEqualToYards(int yards) {
		//TODO switch to enum or something not string checking i.e. currency
		if (getLengthUnit().equalsIgnoreCase("yards")) {
			return yards > getValue();
		}
		if (getLengthUnit().equalsIgnoreCase("feet")) {
			return yards > getValue() * 3;
		}
		throw new IllegalStateException(String.format("Not configured to compare distances of %s length unit", getLengthUnit()));
	}
}
