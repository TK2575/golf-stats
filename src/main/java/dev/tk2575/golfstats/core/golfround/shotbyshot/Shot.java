package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Shot {

	Lie getLie();

	Integer getSequence();

	Distance getDistanceFromTarget();

	MissAngle getMissAngle();

	Distance getMissDistance();

	default Distance getDistance() {
		return Distance.shotDistance(getDistanceFromTarget(), getMissDistance(), getMissAngle());
	}

	Lie getResultLie();

	Integer getCount();

	static ShotStream stream(Collection<Shot> shots) { return new ShotStream(shots); }

	static Shot holed(@NonNull ShotAbbreviation shot) {
		return SimpleShot.holed(shot);
	}

	static Shot of(ShotAbbreviation current, ShotAbbreviation next) {
		return new SimpleShot(current, next);
	}

	default ShotCategory getShotCategory() {
		return ShotCategory.unknown();
	}

	default BigDecimal getStrokesGainedBaseline() {
		return BigDecimal.ZERO;
	}

	default BigDecimal getStrokesGained() {
		return BigDecimal.ZERO;
	}

	static Shot categorize(Hole hole, Shot shot) {
		return new CategorizedShot(hole, shot);
	}

	static Shot strokesGained(Shot shot, BigDecimal strokesGainedBaseline, BigDecimal strokesGained) {
		return new StrokesGainedShot(shot, strokesGainedBaseline, strokesGained);
	}

	static List<Shot> compile(@NonNull List<ShotAbbreviation> list) {
		List<Shot> results = new ArrayList<>();
		ShotAbbreviation current, next;

		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			if (i+1 >= list.size()) {
				results.add(holed(current));
			}
			else {
				next = list.get(i+1);
				results.add(of(current, next));
			}
		}
		return results;
	}
}
