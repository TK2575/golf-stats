package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@ToString
@EqualsAndHashCode
public class RoughLie implements Lie {

	@Override
	public String getLabel() {
		return "Rough";
	}
}
