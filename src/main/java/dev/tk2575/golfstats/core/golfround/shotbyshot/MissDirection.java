package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
class MissDirection implements MissAngle {

	private int angleDegrees;
	private boolean leftMiss;
	private boolean rightMiss;

	MissDirection(char missDirection) {
		if (!MissAngle.SHORTHAND_DIRECTIONS.contains(missDirection)) {
			throw new IllegalArgumentException(missDirection + " not a supported missDirection");
		}

		this.leftMiss = false;
		this.rightMiss = false;
		this.angleDegrees = 0;

		if (missDirection == 'l') {
			this.leftMiss = true;
			this.angleDegrees = 270;
		}
		else if (missDirection == 'r') {
			this.rightMiss = true;
			this.angleDegrees = 90;
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
}
