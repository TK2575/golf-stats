package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class GreenLie implements Lie {

	@Override
	public boolean isGreen() {
		return true;
	}

	@Override
	public String getLabel() {
		return "Green";
	}

	@Override
	public String getAbbrev() {
		return "g";
	}
}
