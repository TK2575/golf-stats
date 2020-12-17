package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.Utils;
import dev.tk2575.golfstats.golfround.shotbyshot.Shot;
import lombok.*;

import java.math.BigDecimal;

public class SimpleShotAnalysis implements ShotAnalysis {

	@Getter private final Shot shot;
	private final Shot result;

	@Getter private final BigDecimal strokesGained;

	public SimpleShotAnalysis(Shot shot, Shot result) {
		this.shot = shot;
		this.result = result;
		this.strokesGained = Utils.roundToTwoDecimalPlaces(this.shot.getStrokesGainedBaseline().subtract(this.result.getStrokesGainedBaseline()).subtract(BigDecimal.valueOf(this.shot.getCount())));
	}
}
