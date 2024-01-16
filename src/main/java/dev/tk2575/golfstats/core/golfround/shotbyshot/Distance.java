package dev.tk2575.golfstats.core.golfround.shotbyshot;

public interface Distance {

	static Distance parse(Lie lie, long value) {
		if (lie.isGreen()) {
			return feet(value);
		}
		else { return yards(value); }
	}

	static Distance difference(Distance distanceFromTarget, Distance missDistance) {
		//TODO adjust difference based on miss angle
		//TODO match distance unit (green = feet, else yards)
		return Distance.yards(distanceFromTarget.getLengthInYards() - missDistance.getLengthInYards());
	}

	Long getValue();

	String getLengthUnit();
	
	Long getLengthInYards();
	
	Long getLengthInFeet();

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
