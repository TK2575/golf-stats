package dev.tk2575.golfstats.core.golfround.shotbyshot;

import dev.tk2575.ObjectStream;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.strokesgained.ShotsGainedComputation;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.tk2575.Utils.percentile;

@Getter
@AllArgsConstructor
public class ShotStream implements ObjectStream<Shot> {

	private final Stream<Shot> stream;
	private final boolean empty;

	public ShotStream(Collection<Shot> shots) {
		this.stream = shots.stream();
		this.empty = shots.isEmpty();
	}
	
	public ShotStream(Stream<Shot> shots) {
		this(shots.toList());
	}

	public static ShotStream empty() { return new ShotStream(Stream.empty(), true); }
	
	public Long countGreatShots() {
		return this.stream.filter(Shot::isGreatShot).count();
	}
	
	public Long countBadShots() {
		return this.stream.filter(Shot::isBadShot).count();
	}
	
	public Long getGreatVsBadShots() {
		List<Shot> list = this.stream.toList();
		return new ShotStream(list).countGreatShots() - new ShotStream(list).countBadShots();
	}

	public Integer totalStrokesAdjusted(Integer par, Integer handicapStrokes) {
		return Math.min(totalStrokes(), par + 2 + handicapStrokes);
	}

	public Integer totalNetStrokes(Integer par, Integer handicapStrokes) {
		return totalStrokesAdjusted(par, handicapStrokes) - handicapStrokes;
	}
	
	public Long p75DrivingDistance() {
		return percentile(this.teeShots().map(shot -> shot.getDistance().getLengthInYards()).toList(), 75L);
	}

	public Integer getPutts() {
		return greenShots().sumInteger(Shot::getCount);
	}

	private Optional<Shot> teeShot() {
		return this.stream.filter(shot -> shot.getLie().is(Lie.tee())).findFirst();
	}

	public ShotStream greenShots() {
		return new ShotStream(this.stream.filter(shot -> shot.getLie().is(Lie.green())), this.empty);
	}
	
	public ShotStream teeShots() {
		return new ShotStream(this.stream.filter(
				shot -> shot.getShotCategory().is(ShotCategory.tee())), 
				this.empty
		);
	}
	
	public boolean isFairwayInRegulation(boolean fairwayPresent) {
		return fairwayPresent && isSecondStrokeFairway();
	}

	private boolean isSecondStrokeFairway() {
		if (isEmpty()) { return false; }
		List<Shot> shotList = this.toList();
		if (shotList.get(0).getCount() > 1) { return false; }
		return shotList.get(1).getLie().is(Lie.fairway());
	}

	public Integer totalStrokes() {
		return sumInteger(Shot::getCount);
	}

	public Map<String, BigDecimal> strokesGainedByShotType() {
		return this.stream.collect(
				Collectors.toUnmodifiableMap(shot -> 
						shot.getShotCategory().getLabel(), 
						Shot::getStrokesGained, 
						BigDecimal::add)
		);
	}

	public BigDecimal teeShotStrokesGainedBaseline() {
		return teeShot().map(Shot::getStrokesGainedBaseline).orElse(BigDecimal.ZERO);
	}

	public BigDecimal totalStrokesGained() {
		return this.stream.map(Shot::getStrokesGained).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	public ShotStream computeStrokesGained(ShotsGainedComputation computer) {
		return new ShotStream(this.stream.map(computer::analyzeShot).toList());
	}

	public ShotStream significantShots() {
		return new ShotStream(this.stream.filter(
				shot -> shot.getStrokesGained().abs().compareTo(new BigDecimal("0.5")) >= 0
		).toList());
	}

	public ShotStream categorize(Hole hole) {
		return new ShotStream(this.stream.map(shot -> Shot.categorize(hole, shot)), this.empty);
	}

	public Long yardsFromTee() {
		return teeShot().map(shot -> shot.getDistanceFromTarget().getValue()).orElse(0L);
	}

	public ShotStream categorized() {
		return new ShotStream(this.stream.filter(shot -> !shot.getShotCategory().equals(ShotCategory.unknown())), this.empty);
	}
}
