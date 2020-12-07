package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class TeeLie implements Lie {

	@Override
	public String getLabel() {
		return "Tee";
	}
}
