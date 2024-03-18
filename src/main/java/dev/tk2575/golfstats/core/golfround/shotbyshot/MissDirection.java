package dev.tk2575.golfstats.core.golfround.shotbyshot;

import lombok.*;

import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
class MissDirection implements MissAngle {
	private boolean leftMiss;
	private boolean rightMiss;
	private final String missShorthand;

	MissDirection(char missShorthand) {
		if (!MissAngle.SHORTHAND_DIRECTIONS.contains(missShorthand)) {
			throw new IllegalArgumentException(missShorthand + " not a supported missDirection");
		}

		this.missShorthand = String.valueOf(missShorthand);
		this.leftMiss = false;
		this.rightMiss = false;

		if (missShorthand == 'l') {
			this.leftMiss = true;
		}
		else if (missShorthand == 'r') {
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
	public String getAbbreviation() {
		return this.missShorthand;
	}

	@Override
	public Optional<Integer> getAngleDegrees() {
		return Optional.empty();
	}
}
