package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

public interface ShotsGainedComputation {

	ShotsGained getShotsGained(Shot shot);
}
