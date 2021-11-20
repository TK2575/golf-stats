package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.Getter;

@Getter
public class SimpleShot implements Shot {
    private final Lie lie;
    private final Distance distanceFromTarget;
    private final MissAngle missAngle;
    private final Distance missDistance;
    private final Integer count;

    SimpleShot(ShotAbbreviation shot, ShotAbbreviation result) {
        this.lie = shot.getLie();
        this.distanceFromTarget = shot.getDistanceFromTarget();
        this.missAngle = result.getPriorShotMissAngle();
        this.missDistance = result.getDistanceFromTarget();
        this.count = shot.getCount();
    }

    static SimpleShot holed(ShotAbbreviation shot) {
        return new SimpleShot(shot);
    }

    private SimpleShot(ShotAbbreviation shot) {
        this.lie = shot.getLie();
        this.distanceFromTarget = shot.getDistanceFromTarget();
        this.missAngle = MissAngle.center();
        this.missDistance = Distance.zero();
        this.count = shot.getCount();
    }
}
