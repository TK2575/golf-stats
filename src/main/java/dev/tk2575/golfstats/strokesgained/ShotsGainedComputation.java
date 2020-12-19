package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

import java.math.BigDecimal;

public interface ShotsGainedComputation {

	static ShotsGainedComputation broadie() {
		return BroadieImputed.getInstance();
	}

	Shot analyzeShot(Shot shot, Shot result);

	default Shot analyzeHoledShot(Shot shot) {
		return analyzeShot(shot, Shot.holed());
	}
}
