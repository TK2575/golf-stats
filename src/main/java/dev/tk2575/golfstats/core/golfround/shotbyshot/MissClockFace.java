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
			throw new IllegalArgumentException(clockFace + " is not a valid clockFace number");
		}

		this.angleDegrees = clockFace == 12 ? 0 : clockFace * 30;
		this.leftMiss = clockFace > 7 && clockFace < 11;
		this.rightMiss = clockFace > 1 && clockFace < 5;
		this.longMiss = clockFace < 2 || clockFace > 10;
		this.shortMiss = clockFace > 4 && clockFace < 8;
	}

	@Override
	public String getMissType() {
		return "Angle";
	}
}
