package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import lombok.*;

import java.math.BigDecimal;

@Getter
public class ShotAnalysis {
	private final int hole;
	private final String lie;
	private final String category;
	private final long distanceValue;
	private final String distanceUnit;
	private final BigDecimal strokesGained;
	//TODO miss angle
	// need to transpose parsed miss angles to the prior shot during object construction

	public ShotAnalysis(int hole, Shot shot) {
		this.hole = hole;
		this.lie = shot.getLie().getLabel();
		this.category = shot.getShotCategory().getLabel();
		this.distanceValue = shot.getDistanceFromTarget().getValue();
		this.distanceUnit = shot.getDistanceFromTarget().getLengthUnit();
		this.strokesGained = shot.getStrokesGained();
	}
}
