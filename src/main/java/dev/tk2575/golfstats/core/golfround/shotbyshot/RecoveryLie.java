package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@EqualsAndHashCode
@ToString
public class RecoveryLie implements Lie {

	@Override
	public String getLabel() {
		return "Recovery";
	}

	@Override
	public String getAbbrev() {
		return "y";
	}

	@Override
	public boolean isRecovery() { return true; }
}
