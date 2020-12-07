package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.golfround.shotbyshot.ShotStream;
import lombok.*;

import java.util.Collection;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ShotByShotHole implements Hole {
	private final Integer number;
	private final Integer index;
	private final Integer par;
	private final Integer handicapStrokes;

	@Getter(AccessLevel.NONE) @ToString.Exclude
	private final Collection<Shot> shots;

	public ShotByShotHole(ShotByShotHole hole, Integer handicapStrokes) {
		this.number = hole.getNumber();
		this.index = hole.getIndex();
		this.par = hole.getPar();
		this.handicapStrokes = handicapStrokes;
		this.shots = hole.getShots().asList();
	}

	@Override
	public ShotStream getShots() { return shots(); }

	private ShotStream shots() { return Shot.stream(this.shots); }

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
