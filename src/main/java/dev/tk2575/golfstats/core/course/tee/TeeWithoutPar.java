package dev.tk2575.golfstats.core.course.tee;

import lombok.*;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class TeeWithoutPar implements Tee {
	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;

	@Override
	public Integer getPar() {
		return 0;
	}

	@Override
	public Integer getHoleCount() {
		return this.rating.compareTo(new BigDecimal("45")) > 0 ? 18 : 9;
	}
}
