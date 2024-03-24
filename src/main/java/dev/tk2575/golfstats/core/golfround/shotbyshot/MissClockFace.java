package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
class MissClockFace implements MissAngle {

	private final int angleDegrees;
	private final boolean leftMiss;
	private final boolean rightMiss;
	private final boolean shortMiss;
	private final boolean longMiss;
	private final int clockFace;

	MissClockFace(int clockFace) {
		if (clockFace <= 0 || clockFace > 12) {
			throw new IllegalArgumentException(clockFace + " is not a valid clockFace number");
		}

		this.clockFace = clockFace;
		this.angleDegrees = clockFace == 12 ? 0 : clockFace * 30;
		this.leftMiss = clockFace > 7 && clockFace < 11;
		this.rightMiss = clockFace > 1 && clockFace < 5;
		this.longMiss = clockFace < 2 || clockFace > 10;
		this.shortMiss = clockFace > 4 && clockFace < 8;
	}
	
	@Override
	public Optional<Integer> getAngleDegrees() {
		return Optional.of(angleDegrees);
	}

	@Override
	public String getAbbreviation() {
		return String.valueOf(clockFace);
	}

}
