package dev.tk2575.golfstats.golfround.shotbyshot;

import dev.tk2575.golfstats.ObjectStream;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class ShotStream implements ObjectStream<Shot> {
	private final Stream<Shot> stream;
	private final boolean empty;

	public ShotStream(Collection<Shot> shots) {
		this.stream = shots.stream();
		this.empty = shots.isEmpty();
	}

	public static ShotStream empty() { return new ShotStream(Stream.empty(), true); }

	public Integer totalStrokesAdjusted(Integer par, Integer handicapStrokes) {
		return Math.min(totalStrokes(), par+2+handicapStrokes);
	}

	public Integer totalNetStrokes(Integer par, Integer handicapStrokes) {
		return totalStrokesAdjusted(par, handicapStrokes) - handicapStrokes;
	}

	public Integer getPutts() {
		return greenShots().sumInteger(Shot::getCount);
	}

	private ShotStream greenShots() {
		return new ShotStream(this.stream.filter(s -> s.getLie().isGreen()), this.empty);
	}

	public boolean isFairwayInRegulation(boolean fairwayPresent) {
		return fairwayPresent && isSecondStrokeFairway();
	}

	private boolean isSecondStrokeFairway() {
		if (isEmpty()) return false;

		//TODO refactor without collecting to List?
		List<Shot> shotList = this.asList();
		if (shotList.get(0).getCount() > 1) return false;
		return shotList.get(1).getLie().isFairway();
	}

	public Integer totalStrokes() {
		return sumInteger(Shot::getCount);
	}
}
