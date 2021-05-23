package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
public class StrokesGainedShot implements Shot {

	private final Lie lie;
	private final Distance distance;
	private final MissAngle missAngle;
	private final Integer count;
	private final ShotCategory shotCategory;
	private final BigDecimal strokesGainedBaseline;
	private final BigDecimal strokesGained;

	public StrokesGainedShot(Shot shot, BigDecimal strokesGainedBaseline, BigDecimal strokesGained) {
		this.lie = shot.getLie();
		this.distance = shot.getDistance();
		this.missAngle = shot.getMissAngle();
		this.count = shot.getCount();
		this.strokesGainedBaseline = strokesGainedBaseline;
		this.strokesGained = strokesGained;
		this.shotCategory = shot.getShotCategory();
	}
}
