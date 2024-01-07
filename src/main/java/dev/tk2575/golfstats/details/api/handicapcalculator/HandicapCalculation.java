package dev.tk2575.golfstats.details.api.handicapcalculator;

import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.handicapindex.StablefordQuota;
import lombok.*;

import java.math.BigDecimal;

@Getter
public class HandicapCalculation {
	private final String teeName;
	private final String golfer;
	private final String indexLabel;
	private final BigDecimal indexValue;
	private final Integer strokes;
	private final Integer quota;

	public HandicapCalculation(@NonNull TeeDTO teeDTO, 
							   @NonNull String golfer, 
							   @NonNull String indexLabel, 
							   @NonNull BigDecimal indexValue) 
	{
		Tee tee = Tee.of(
				teeDTO.getName(), 
				teeDTO.getRating(), 
				teeDTO.getSlope(), 
				teeDTO.getPar()
		);
		this.teeName = tee.getName();
		this.golfer = golfer;
		this.indexLabel = indexLabel;
		this.indexValue = indexValue;
		this.strokes = tee.handicapStrokes(this.indexValue);
		this.quota = StablefordQuota.compute(tee, this.strokes);
	}
}
