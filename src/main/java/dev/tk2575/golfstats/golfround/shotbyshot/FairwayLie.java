package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class FairwayLie implements Lie {

	@Override
	public String getLabel() {
		return "Fairway";
	}

	@Override
	public boolean isFairway() { return true; }
}
