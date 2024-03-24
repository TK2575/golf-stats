package dev.tk2575.golfstats.core.golfround;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@EqualsAndHashCode
class SimpleHoleScore implements Hole {

	private final Integer number;
	private final Integer index;
	private final Integer par;
	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final boolean fairwayInRegulation;
	private final Integer putts;
	private final Integer netStrokes;

	@Override
	public Hole setNumber(Integer number) {
		return this.toBuilder().number(number).build();
	}

	@Override
	public Hole setIndex(Integer index) {
		return this.toBuilder().index(index).build();
	}

	@Override
	public Hole applyNetDoubleBogey(Integer handicapStrokes) {
		return new SimpleHoleScore(this, computeNetDoubleBogey(handicapStrokes));
	}

	private SimpleHoleScore(Hole hole, Integer handicapStrokes) {
		this.number = hole.getNumber();
		this.index = hole.getIndex();
		this.par = hole.getPar();
		this.strokes = hole.getStrokes();
		this.putts = hole.getPutts();
		this.strokesAdjusted = Math.min(this.strokes, this.par + 2 + handicapStrokes);
		this.netStrokes = this.strokesAdjusted - handicapStrokes;
		this.fairwayInRegulation = hole.isFairwayPresent() && hole.isFairwayInRegulation();
	}

	SimpleHoleScore(Integer number, Integer index, Integer par, Integer strokes, boolean fairwayInRegulation, Integer putts) {
		this.number = number;
		this.index = index;
		this.par = par;
		this.strokes = strokes;
		this.putts = putts;
		this.strokesAdjusted = this.strokes;
		this.netStrokes = this.strokes;
		this.fairwayInRegulation = this.isFairwayPresent() && fairwayInRegulation;
	}
	
	SimpleHoleScore(Integer number, Integer index, Integer par, Integer strokes, Integer strokesAdjusted, Integer netStrokes, Integer putts, boolean fairwayInRegulation) {
		this.number = number;
		this.index = index;
		this.par = par;
		this.strokes = strokes;
		this.strokesAdjusted = strokesAdjusted;
		this.netStrokes = netStrokes;
		this.putts = putts;
		this.fairwayInRegulation = fairwayInRegulation;
	}
}
