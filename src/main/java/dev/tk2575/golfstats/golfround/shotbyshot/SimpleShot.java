package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SimpleShot implements Shot {

	private final Lie lie;
	private final Distance distance;
	private final MissAngle missAngle;
	private final Integer count;
	private final ShotCategory shotCategory;

	public SimpleShot(Lie lie, Distance distance, MissAngle missAngle, Integer count) {
		this.lie = lie;
		this.distance = distance;
		this.missAngle = missAngle;
		this.count = count;
		this.shotCategory = ShotCategory.compute(this);
	}
}
