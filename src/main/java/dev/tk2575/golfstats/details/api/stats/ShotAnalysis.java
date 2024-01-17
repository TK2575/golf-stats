package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ShotAnalysis implements StatsApiValueSupplier {
	private final int hole;
	private final int sequence;
	private final String lie;
	private final String category;
	private final long distanceFromHole;
	private final String distanceFromHoleUnit;
	private final long shotDistance;
	private final String shotDistanceUnit;
	private final BigDecimal strokesGained;
	private final String resultLie;
	private final String missType;
	private final long missDistanceValue;
	private final String missDistanceUnit;
	private final long missAngle;
	private final String missDescription;
	private final long count;

	public ShotAnalysis(int hole, Shot shot) {
		this.hole = hole;
		this.sequence = shot.getSequence();
		this.lie = shot.getLie().getLabel();
		this.category = shot.getShotCategory().getLabel();
		this.distanceFromHole = shot.getDistanceFromTarget().getValue();
		this.distanceFromHoleUnit = shot.getDistanceFromTarget().getLengthUnit();
		this.shotDistance = shot.getDistance().getValue();
		this.shotDistanceUnit = shot.getDistance().getLengthUnit();
		this.strokesGained = shot.getStrokesGained();
		this.resultLie = shot.getResultLie().getLabel();
		this.missType = shot.getMissAngle().getMissType();
		this.missDistanceValue = shot.getMissDistance().getValue();
		this.missDistanceUnit = shot.getMissDistance().getLengthUnit();
		this.missAngle = shot.getMissAngle().getAngleDegrees();
		this.missDescription = shot.getMissAngle().getDescription();
		this.count = shot.getCount();
	}
	
	public static List<String> headers() {
		return List.of(
				"Hole",
				"Sequence",
				"Lie",
				"Category",
				"Target Distance",
				"Target Distance Unit",
				"Shot Distance",
				"Shot Distance Unit",
				"SG",
				"Result Lie",
				"Miss Type",
				"Miss Distance",
				"Unit",
				"Miss Angle",
				"Miss Description",
				"Count"
		);
	}
	
	public List<String> values() {
		return List.of(
				String.valueOf(hole),
				String.valueOf(sequence),
				lie,
				category,
				String.valueOf(distanceFromHole),
				distanceFromHoleUnit,
				String.valueOf(shotDistance),
				shotDistanceUnit,
				strokesGained.toPlainString(),
				resultLie,
				missType,
				String.valueOf(missDistanceValue),
				missDistanceUnit,
				String.valueOf(missAngle),
				missDescription,
				String.valueOf(count)
		);
	}
}
