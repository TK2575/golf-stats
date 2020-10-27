package dev.tk2575.golfstats.course.tee;

import lombok.*;

import java.math.BigDecimal;

@Getter
public class TeeWithYardage extends SimpleTee implements TeeYardage  {

	private final Long yards;

	public TeeWithYardage(String name, BigDecimal rating,BigDecimal slope, Integer par, Long yards) {
		super(name, rating, slope, par);
		this.yards = yards;
	}
}
