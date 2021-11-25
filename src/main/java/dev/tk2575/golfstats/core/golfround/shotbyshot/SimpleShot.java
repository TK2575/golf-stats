package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@Getter
public class SimpleShot implements Shot {
    private final Lie lie;
    private final Distance distanceFromTarget;
    private final MissAngle missAngle;
    private final Distance missDistance;
	private final Lie resultLie;
    private final Integer count;

    SimpleShot(ShotAbbreviation shot, ShotAbbreviation result) {
        this.lie = shot.getLie();
        this.distanceFromTarget = shot.getDistanceFromTarget();
        this.missAngle = result.getPriorShotMissAngle();
        this.missDistance = result.getDistanceFromTarget();
	    this.resultLie = result.getLie();
        this.count = shot.getCount();
    }

    static SimpleShot holed(@NonNull ShotAbbreviation shot) {
        return new SimpleShot(shot);
    }

    private SimpleShot(ShotAbbreviation shot) {
        this.lie = shot.getLie();
        this.distanceFromTarget = shot.getDistanceFromTarget();
        this.missAngle = MissAngle.center();
        this.missDistance = Distance.zero();
	    this.resultLie = Lie.hole();
        this.count = shot.getCount();
    }
}
