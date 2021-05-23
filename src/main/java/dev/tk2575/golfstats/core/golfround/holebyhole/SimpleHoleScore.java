package dev.tk2575.golfstats.core.golfround.holebyhole;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class SimpleHoleScore implements Hole {
	private final Integer number;
	private final Integer index;
	private final Integer par;
	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final boolean fairwayInRegulation;
	private final Integer putts;
	private final Integer netStrokes;

	@Override
	public Hole applyNetDoubleBogey(Integer handicapStrokes) {
		return new SimpleHoleScore(this, computeNetDoubleBogey(handicapStrokes));
	}

	private SimpleHoleScore(Hole hole, Integer handicapStrokes) {
		this.number = hole.getNumber();
		this.index = hole.getIndex();
		this.par = hole.getPar();
		this.strokes = hole.getStrokes();
		this.fairwayInRegulation = hole.isFairwayInRegulation();
		this.putts = hole.getPutts();
		this.strokesAdjusted = Math.min(this.strokes, this.par+2+handicapStrokes);
		this.netStrokes = this.strokesAdjusted - handicapStrokes;
	}
}
