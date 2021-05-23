package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class UnknownShotCategory implements ShotCategory {

	@Override
	public String getLabel() {
		return "Unknown";
	}
}
