package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
public class MissDirection implements MissAngle {

	private boolean leftMiss;
	private boolean rightMiss;

	public MissDirection(char missDirection) {
		if (!MissAngle.SHORTHAND_DIRECTIONS.contains(missDirection)) {
			throw new IllegalArgumentException(missDirection + " not a supported missDirection");
		}

		this.leftMiss = false;
		this.rightMiss = false;

		if (missDirection == 'l') this.leftMiss = true;
		if (missDirection == 'r') this.rightMiss = true;
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
