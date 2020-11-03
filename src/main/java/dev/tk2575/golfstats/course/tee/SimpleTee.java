package dev.tk2575.golfstats.course.tee;

import lombok.*;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.NONE)
public class SimpleTee implements Tee {

	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final Integer par;
	private final Integer holeCount;

	public SimpleTee(String name, BigDecimal rating, BigDecimal slope,
	                 Integer par) {
		this.name = name;
		this.slope = slope;
		this.par = par;
		this.rating = correctCourseRating(this.par, rating);
		this.holeCount = this.par < 54 ? 9 : 18;
	}

	private BigDecimal correctCourseRating(Integer par, BigDecimal rating) {
		if (rating.subtract(BigDecimal.valueOf(par)).compareTo(BigDecimal.TEN) > 0) {
			return rating.divide(BigDecimal.valueOf(2), 1, HALF_UP);
		}
		return rating;
	}
}
