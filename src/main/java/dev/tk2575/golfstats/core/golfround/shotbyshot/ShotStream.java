package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.ObjectStream;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.strokesgained.ShotsGainedComputation;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
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
		return Math.min(totalStrokes(), par + 2 + handicapStrokes);
	}

	public Integer totalNetStrokes(Integer par, Integer handicapStrokes) {
		return totalStrokesAdjusted(par, handicapStrokes) - handicapStrokes;
	}

	public Integer getPutts() {
		return greenShots().sumInteger(Shot::getCount);
	}

	private Optional<Shot> teeShot() {
		return this.stream.filter(shot -> shot.getLie().isTee()).findFirst();
	}

	private ShotStream greenShots() {
		return new ShotStream(this.stream.filter(shot -> shot.getLie().isGreen()), this.empty);
	}

	public boolean isFairwayInRegulation(boolean fairwayPresent) {
		return fairwayPresent && isSecondStrokeFairway();
	}

	private boolean isSecondStrokeFairway() {
		if (isEmpty()) { return false; }

		//TODO refactor without collecting to List?
		List<Shot> shotList = this.asList();
		if (shotList.get(0).getCount() > 1) { return false; }
		return shotList.get(1).getLie().isFairway();
	}

	public Integer totalStrokes() {
		return sumInteger(Shot::getCount);
	}

	public Map<String, BigDecimal> strokesGainedByShotType() {
		return this.stream.collect(Collectors.toUnmodifiableMap(shot -> shot.getShotCategory()
		                                                                    .getLabel(), Shot::getStrokesGained, BigDecimal::add));
	}

	public BigDecimal teeShotStrokesGainedBaseline() {
		return teeShot().map(Shot::getStrokesGainedBaseline).orElse(BigDecimal.ZERO);
	}

	public BigDecimal totalStrokesGained() {
		return this.stream.map(Shot::getStrokesGained).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	public ShotStream computeStrokesGained(ShotsGainedComputation computer) {
		List<Shot> shots = new ArrayList<>();
		Shot shot = null;

		//TODO refactor without collecting to List?
		for (Shot result : asList()) {
			if (shot != null) {
				shots.add(computer.analyzeShot(shot, result));
			}
			shot = result;
		}
		shots.add(computer.analyzeHoledShot(shot));
		return new ShotStream(shots);

	}

	public ShotStream categorize(Hole hole) {
		return new ShotStream(this.stream.map(shot -> Shot.categorize(hole, shot)), this.empty);
	}

	public Long yardsFromTee() {
		return teeShot().map(shot -> shot.getDistance().getValue()).orElse(0L);
	}

	public ShotStream categorized() {
		return new ShotStream(this.stream.filter(shot -> !shot.getShotCategory().equals(ShotCategory.unknown())), this.empty);
	}
}
