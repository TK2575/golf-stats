package dev.tk2575.golfstats.core.course.tee;

import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
class TeeWithYardage extends SimpleTee {

	private final Long yards;

	TeeWithYardage(String name, BigDecimal rating, BigDecimal slope, Integer par, Long yards) {
		super(name, rating, slope, par);
		this.yards = yards;
	}
}
