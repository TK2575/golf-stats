package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import dev.tk2575.golfstats.core.strokesgained.ShotsGainedComputation;
import lombok.*;

import java.util.Collection;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
class ShotByShotHole implements Hole {
	private final Integer number;
	private final Integer index;
	private final Integer par;
	private final Integer handicapStrokes;

	@Getter(AccessLevel.NONE) @ToString.Exclude
	private final Collection<Shot> shots;

	ShotByShotHole(ShotByShotHole hole, Integer handicapStrokes) {
		this.number = hole.getNumber();
		this.index = hole.getIndex();
		this.par = hole.getPar();
		this.handicapStrokes = handicapStrokes;
		this.shots = hole.getShots().toList();
	}

	ShotByShotHole(Integer number, Integer index, Integer par, Collection<Shot> shots, ShotsGainedComputation computer) {
		this.number = number;
		this.index = index;
		this.par = par;
		this.handicapStrokes = 0;
		this.shots = Shot.stream(shots).categorize(this).computeStrokesGained(computer).toList();
	}

	@Override
	public ShotStream getShots() { return shots(); }

	private ShotStream shots() { return Shot.stream(this.shots); }

	@Override
	public Hole setNumber(Integer number) {
		return this.toBuilder().number(number).build();
	}

	@Override
	public Hole setIndex(Integer index) {
		return this.toBuilder().index(index).build();
	}

	@Override
	public Integer getStrokes() {
		return shots().totalStrokes();
	}

	@Override
	public Integer getStrokesAdjusted() {
		return shots().totalStrokesAdjusted(this.par, this.handicapStrokes);
	}

	@Override
	public Integer getNetStrokes() {
		return shots().totalNetStrokes(this.par, this.handicapStrokes);
	}

	@Override
	public Hole applyNetDoubleBogey(Integer handicapStrokes) {
		return new ShotByShotHole(this, computeNetDoubleBogey(handicapStrokes));
	}

	@Override
	public Integer getPutts() {
		return shots().getPutts();
	}

	@Override
	public boolean isFairwayInRegulation() {
		return shots().isFairwayInRegulation(this.isFairwayPresent());
	}

}
