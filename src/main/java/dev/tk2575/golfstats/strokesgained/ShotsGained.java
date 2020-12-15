package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

import java.math.BigDecimal;

public interface ShotsGained {

	static ShotsGained of(Shot shot, BigDecimal baseLine, Shot result, BigDecimal resultBaseLine) {
		return new SimpleShotsGained(shot, baseLine, result, resultBaseLine);
	}

	Shot getShot();

	Shot getResult();

	BigDecimal getBaseline();

	BigDecimal getResultBaseline();

	BigDecimal getStrokesGained();
}
