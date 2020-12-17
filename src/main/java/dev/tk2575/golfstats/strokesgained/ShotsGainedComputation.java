package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

public interface ShotsGainedComputation {

	ShotAnalysis analyzeShot(Shot shot, Shot result);

	default ShotAnalysis analyzeHoledShot(Shot shot) {
		return analyzeShot(shot, Shot.holed());
	}
}
