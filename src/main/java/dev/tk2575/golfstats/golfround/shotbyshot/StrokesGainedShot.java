package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

import java.math.BigDecimal;

@Getter
public class StrokesGainedShot implements Shot {

	private final Lie lie;
	private final Distance distance;
	private final MissAngle missAngle;
	private final Integer count;
	private final BigDecimal strokesGainedBaseline;

	public StrokesGainedShot(Shot shot, BigDecimal strokesGainedBaseline) {
		this.lie = shot.getLie();
		this.distance = shot.getDistance();
		this.missAngle = shot.getMissAngle();
		this.count = shot.getCount();
		this.strokesGainedBaseline = strokesGainedBaseline;
	}
}
