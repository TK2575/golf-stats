package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

import java.math.BigDecimal;

public interface ShotAnalysis {

	static ShotAnalysis of(Shot shot, Shot result) {
		return new SimpleShotAnalysis(shot, result);
	}

	Shot getShot();

	BigDecimal getStrokesGained();

	//TODO result analysis i.e. miss angle
}
