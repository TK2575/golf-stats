package dev.tk2575.golfstats.golfround.holebyhole;

import java.util.Collection;

public interface Hole {

	static HoleStream stream(Collection<Hole> holes) {
		return new HoleStream(holes);
	}

	Integer getNumber();

	Integer getIndex();

	Integer getPar();

	Integer getStrokes();

	Integer getStrokesAdjusted();

	Integer getNetStrokes();

	Hole applyNetDoubleBogey(Integer handicapStrokes);

	Integer getPutts();

	boolean isFairwayInRegulation();

	default boolean isFairwayPresent() {
		return getPar() > 3;
	}

	default boolean isGreenInRegulation() {
		return getPar() - 2 >= getStrokes() - getPutts();
	}
}
