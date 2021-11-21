package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.shotbyshot.MissAngle;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import lombok.*;

import java.math.BigDecimal;

@Getter
public class ShotAnalysis {
	private final int hole;
	private final String lie;
	private final String category;
	private final long distanceValue;
	private final String distanceUnit;
	private final BigDecimal strokesGained;
	private final String resultLie;
	private final long missDistanceValue;
	private final String missDistanceUnit;
	private final long missAngle;
	private final String missDescription;
	private final long count;

	public ShotAnalysis(int hole, Shot shot) {
		this.hole = hole;
		this.lie = shot.getLie().getLabel();
		this.category = shot.getShotCategory().getLabel();
		this.distanceValue = shot.getDistanceFromTarget().getValue();
		this.distanceUnit = shot.getDistanceFromTarget().getLengthUnit();
		this.strokesGained = shot.getStrokesGained();
		this.resultLie = shot.getResultLie().getLabel();
		this.missDistanceValue = shot.getMissDistance().getValue();
		this.missDistanceUnit = shot.getMissDistance().getLengthUnit();
		this.missAngle = shot.getMissAngle().getAngleDegrees();
		this.missDescription = shot.getMissAngle().getDescription();
		this.count = shot.getCount();
	}
}
