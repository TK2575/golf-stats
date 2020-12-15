package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;
import lombok.*;

import java.math.BigDecimal;

@Getter
public class SimpleShotsGained implements ShotsGained {

	//TODO have Shot include baseline

	private final Shot shot;
	private final Shot result;

	private final BigDecimal baseline;
	private final BigDecimal resultBaseline;

	private final BigDecimal strokesGained;

	public SimpleShotsGained(Shot shot, BigDecimal baseLine, Shot result, BigDecimal resultBaseLine) {
		this.shot = shot;
		this.result = result;
		this.baseline = baseLine;
		this.resultBaseline = resultBaseLine;

		this.strokesGained = baseLine.subtract(resultBaseLine).subtract(BigDecimal.valueOf(shot.getCount()));
	}
}
