package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
public class MissClockFace implements MissAngle {

	private final int missAngle;
	private final boolean leftMiss;
	private final boolean rightMiss;
	private final boolean shortMiss;
	private final boolean longMiss;

	public MissClockFace(int clockFace) {
		if (clockFace < 0 || clockFace > 12) {
			throw new IllegalArgumentException(clockFace + " is not a valid clockFace");
		}

		this.missAngle = clockFace == 0 ? 12 : clockFace;
		this.leftMiss = this.missAngle > 6 && this.missAngle < 12;
		this.rightMiss = this.missAngle < 6;
		this.longMiss = this.missAngle < 3 || this.missAngle > 9;
		this.shortMiss = this.missAngle > 3 && this.missAngle < 9;
	}
}
