package dev.tk2575.golfstats.golfround.shotbyshot;

import dev.tk2575.golfstats.golfround.holebyhole.Hole;

public interface ShotCategory {

	static ShotCategory unknown() {
		return new UnknownShotCategory();
	}

	static ShotCategory parse(Hole hole, Shot shot) {
		if (shot.getLie().isGreen()) {
			return new GreenShotCategory();
		}
		if (shot.getDistance().isLessThanOrEqualToYards(30)) {
			return new AroundGreenShotCategory();
		}
		if (shot.getLie().isTee() && hole.getPar() > 3) {
			return new TeeShotCategory();
		}
		return new ApproachShotCategory();
	}

	String getLabel();

}
