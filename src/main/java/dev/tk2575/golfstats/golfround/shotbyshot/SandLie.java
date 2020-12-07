package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class SandLie implements Lie {

	@Override
	public String getLabel() {
		return "Sand";
	}
}
