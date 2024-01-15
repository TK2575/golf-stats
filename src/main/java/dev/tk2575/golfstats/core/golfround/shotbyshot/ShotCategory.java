package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.golfstats.core.golfround.Hole;

public interface ShotCategory {

	static ShotCategory unknown() {
		return new UnknownShotCategory();
	}

	static ShotCategory parse(Hole hole, Shot shot) {
		if (shot.getLie().isGreen()) {
			return new GreenShotCategory();
		}
		if (shot.getLie().isRecovery()) {
			return new RecoveryShotCategory();
		}
		if (shot.getDistanceFromTarget().isLessThanOrEqualToYards(30)) {
			return new AroundGreenShotCategory();
		}
		if (shot.getLie().isTee() && hole.getPar() > 3) {
			return new TeeShotCategory();
		}
		return new ApproachShotCategory();
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
