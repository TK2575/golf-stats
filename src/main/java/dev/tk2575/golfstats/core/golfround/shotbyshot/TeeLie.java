package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class TeeLie implements Lie {

	@Override
	public String getLabel() {
		return "Tee";
	}

	@Override
	public String getAbbrev() {
		return "t";
	}

	@Override
	public boolean isTee() { return true; }
}
