package dev.tk2575.golfstats.core.course.tee;

import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.RoundingMode.HALF_UP;

@Getter
@ToString
class SimpleTee implements Tee {

	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final Integer par;
	private final Integer holeCount;

	SimpleTee(String name, BigDecimal rating, BigDecimal slope,
	                 Integer par) {
		this.name = name;
		this.slope = slope;
		this.par = par;
		this.rating = Tee.correctCourseRating(this.par, rating);
		this.holeCount = this.par < 54 ? 9 : 18;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!Tee.class.isAssignableFrom(object.getClass())) {
			return false;
		}
		Tee other = (Tee) object;
		if (getRating().compareTo(other.getRating()) != 0) {
			return false;
		}
		if (getSlope().compareTo(other.getSlope()) != 0) {
			return false;
		}
		return Objects.equals(getPar(), other.getPar());
	}

	@Override
	public int hashCode() {
		return Objects.hash(rating, slope, par);
	}
}
