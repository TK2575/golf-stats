package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.golfstats.core.golfround.Hole;

public interface ShotCategory {

	static ShotCategory unknown() {
		return new UnknownShotCategory();
	}

	static ShotCategory parse(Hole hole, Shot shot) {
		if (shot.getLie().is(Lie.green())) {
			return new GreenShotCategory();
		}
		if (shot.getLie().is(Lie.recovery())) {
			return new RecoveryShotCategory();
		}
		if (shot.getDistanceFromTarget().isLessThanOrEqualToYards(30)) {
			return new AroundGreenShotCategory();
		}
		if (shot.getLie().is(Lie.tee()) && hole.getPar() > 3) {
			return new TeeShotCategory();
		}
		return new ApproachShotCategory();
	}
	
	default boolean is(ShotCategory other) {
		return this.getLabel().equals(other.getLabel());
	}
	
	static ShotCategory tee() {
		return new TeeShotCategory();
	}
	
	static ShotCategory approach() {
		return new ApproachShotCategory();
	}
	
	static ShotCategory aroundGreen() {
		return new AroundGreenShotCategory();
	}
	
	static ShotCategory green() {
		return new GreenShotCategory();
	}
	
	static ShotCategory recovery() {
		return new RecoveryShotCategory();
	}

	String getLabel();

}
