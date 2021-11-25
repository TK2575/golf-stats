package dev.tk2575.golfstats.core.strokesgained;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;

public interface ShotsGainedComputation {

	static ShotsGainedComputation broadie() {
		return BroadieImputed.getInstance();
	}

	Shot analyzeShot(Shot shot);
}
