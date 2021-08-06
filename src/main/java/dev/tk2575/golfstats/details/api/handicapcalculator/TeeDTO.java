package dev.tk2575.golfstats.details.api.handicapcalculator;

import lombok.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class TeeDTO {
	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final Integer par;
}
