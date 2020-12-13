package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

import java.math.BigDecimal;

public interface ShotsGained {

	Shot getShot();

	BigDecimal getBaseline();

	BigDecimal getStrokesGained();
}
