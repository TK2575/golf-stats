package dev.tk2575.golfstats.core.golfround.shotbyshot;

public interface Distance {

	static Distance parse(Lie lie, long value) {
		if (lie.is(Lie.green())) {
			return feet(value);
		}
		else { return yards(value); }
	}

	static Distance shotDistance(Distance distanceFromTarget, Distance missDistance, MissAngle missAngle) {
		if (missAngle.getAngleDegrees().isEmpty()) {
			return distanceFromTarget.subtract(missDistance);
		} 
		int missAngleDegrees = missAngle.getAngleDegrees().get();
		if (missAngleDegrees == 0) {
			return distanceFromTarget.add(missDistance);
		}
		if (missAngleDegrees == 180) {
			return distanceFromTarget.subtract(missDistance);
		}
		return distanceFromTarget.computeLawOfCosineDistance(missDistance, missAngleDegrees);
	}

	default Distance computeLawOfCosineDistance(Distance missDistance, Integer missAngle) {
		double target = getValue(); // y
		double miss = convertToSameUnit(missDistance).getValue(); // x
		var oppositeAngle = (missAngle > 180 ? missAngle - 180 : 180 - missAngle); // a -> b
		//z^2 = x^2 + y^2 - 2xyCos(b)
		double shotDistSqrd = 
				Math.pow(miss,2) + Math.pow(target,2) - (2 * miss * target * Math.cos(Math.toRadians(oppositeAngle))); 
		return ofSameUnit(Math.round(Math.sqrt(shotDistSqrd)));
	}

	Long getValue();

	String getLengthUnit();
	
	Long getLengthInYards();
	
	Long getLengthInFeet();
	
	Distance add(Distance distance);
	
	Distance subtract(Distance distance);
	
	Distance convertToSameUnit(Distance distance);
	
	Distance ofSameUnit(long value);

	static Distance feet(long value) {
		return new FeetDistance(value);
	}

	static Distance yards(long value) {
		return new YardsDistance(value);
	}

	static Distance zero() {
		return new ZeroDistance();
	}

	boolean isLessThanOrEqualToYards(int yards);
	
	static Distance of(long value, String lengthUnit) {
		if (lengthUnit.equalsIgnoreCase("feet")) {
			return feet(value);
		}
		else { return yards(value); }
	}
}
