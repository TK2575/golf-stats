package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.*;

@Getter
public class CategorizedShot implements Shot {

	private final Lie lie;
	private final Integer sequence;
	private final Distance distanceFromTarget;
	private final MissAngle missAngle;
	private final Distance missDistance;
	private final Integer count;
	private final Lie resultLie;
	private final ShotCategory shotCategory;

	public CategorizedShot(Hole hole, Shot shot) {
		this.lie = shot.getLie();
		this.sequence = shot.getSequence();
		this.distanceFromTarget = shot.getDistanceFromTarget();
		this.missAngle = shot.getMissAngle();
		this.missDistance = shot.getMissDistance();
		this.count = shot.getCount();
		this.resultLie = shot.getResultLie();
		this.shotCategory = ShotCategory.parse(hole, this);
	}
}
