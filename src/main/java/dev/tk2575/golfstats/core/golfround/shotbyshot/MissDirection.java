package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
class MissDirection implements MissAngle {
	private boolean leftMiss;
	private boolean rightMiss;

	MissDirection(char missDirection) {
		if (!MissAngle.SHORTHAND_DIRECTIONS.contains(missDirection)) {
			throw new IllegalArgumentException(missDirection + " not a supported missDirection");
		}

		this.leftMiss = false;
		this.rightMiss = false;

		if (missDirection == 'l') {
			this.leftMiss = true;
		}
		else if (missDirection == 'r') {
			this.rightMiss = true;
		}
	}

	@Override
	public boolean isShortMiss() {
		return false;
	}

	@Override
	public boolean isLongMiss() {
		return false;
	}

	@Override
	public Optional<Integer> getAngleDegrees() {
		return Optional.empty();
	}
}
