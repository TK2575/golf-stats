package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class GreenLie implements Lie {

	@Override
	public String getLabel() {
		return "Green";
	}

	@Override
	public String getAbbrev() {
		return "g";
	}
}
