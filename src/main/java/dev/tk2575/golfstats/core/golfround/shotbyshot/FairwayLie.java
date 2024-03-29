package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class FairwayLie implements Lie {

	@Override
	public String getLabel() {
		return "Fairway";
	}

	@Override
	public String getAbbrev() {
		return "f";
	}
	
}
