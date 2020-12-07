package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class SimpleShot implements Shot {

	private final Lie lie;
	private final Distance distance;
	private final MissAngle missAngle;
	private final Integer count;
}
