package dev.tk2575.golfstats.golfround;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

public interface Tee {

	static Tee compositeTee(Tee tee1, Tee tee2) {
		if (tee1.equals(tee2)) {
			return tee1;
		}
		return new CompositeTee(tee1, tee2);
	}

	static Tee newTee(String teeName, BigDecimal courseRating, BigDecimal slope,
	                  Integer par) {
		return new SimpleTee(teeName, courseRating, slope, par);
	}

	String getName();

	BigDecimal getRating();

	BigDecimal getSlope();

	Integer getPar();

	default BigDecimal correctCourseRating(Integer par, BigDecimal rating) {
		if (rating.subtract(BigDecimal.valueOf(par))
		          .compareTo(BigDecimal.TEN) > 0) {
			return rating.divide(new BigDecimal("2"), HALF_UP)
			             .setScale(2, HALF_UP);
		}
		return rating;
	}
}
