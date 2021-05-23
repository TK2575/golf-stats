package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.golfstats.core.golfround.holebyhole.Hole;
import lombok.*;

@Getter
public class CategorizedShot implements Shot {

	private final Lie lie;
	private final Distance distance;
	private final MissAngle missAngle;
	private final Integer count;
	private final ShotCategory shotCategory;

	public CategorizedShot(Hole hole, Shot shot) {
		this.lie = shot.getLie();
		this.distance = shot.getDistance();
		this.missAngle = shot.getMissAngle();
		this.count = shot.getCount();
		this.shotCategory = ShotCategory.parse(hole, this);
	}
}
