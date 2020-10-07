package dev.tk2575.golfstats.golfround;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
public class SimpleTee implements Tee {

	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final Integer par;

	public SimpleTee(String name, BigDecimal rating, BigDecimal slope,
	                 Integer par) {
		this.name = name;
		this.slope = slope;
		this.par = par;
		this.rating = correctCourseRating(this.par, rating);
	}
}
