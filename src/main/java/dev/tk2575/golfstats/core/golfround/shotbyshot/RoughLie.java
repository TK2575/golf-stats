package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@ToString
@EqualsAndHashCode
public class RoughLie implements Lie {

	@Override
	public String getLabel() {
		return "Rough";
	}

	@Override
	public String getAbbrev() {
		return "r";
	}
}
