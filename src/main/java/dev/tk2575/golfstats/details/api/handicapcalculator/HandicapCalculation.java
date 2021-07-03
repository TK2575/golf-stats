package dev.tk2575.golfstats.details.api.handicapcalculator;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class HandicapCalculation {
	private final String teeName;
	private final String golfer;
	private final Integer strokes;
	private final Integer quota;
}
