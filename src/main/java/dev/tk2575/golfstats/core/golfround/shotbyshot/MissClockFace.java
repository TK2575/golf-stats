package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
class MissClockFace implements MissAngle {

	private final int angleDegrees;
	private final boolean leftMiss;
	private final boolean rightMiss;
	private final boolean shortMiss;
	private final boolean longMiss;

	MissClockFace(int clockFace) {
		if (clockFace <= 0 || clockFace > 12) {
			throw new IllegalArgumentException(clockFace + " is not a valid clockFace");
		}

		this.angleDegrees = clockFace == 12 ? 0 : clockFace * 30;
		this.leftMiss = clockFace > 6 && clockFace < 12;
		this.rightMiss = clockFace < 6;
		this.longMiss = clockFace < 3 || clockFace > 9;
		this.shortMiss = clockFace > 3 && clockFace < 9;
	}
}
