package dev.tk2575.golfstats.golfround.holebyhole;

import java.util.Collection;

public interface Hole {

	Integer getNumber();

	Integer getIndex();

	Integer getPar();

	Integer getStrokes();

	default boolean isFairwayPresent() {
		return getPar() > 3;
	}

	boolean isFairwayInRegulation();

	default boolean isGreenInRegulation() {
		return getPar() - 2 >= getStrokes() - getPutts();
	}

	Integer getPutts();

	static HoleStream stream(Collection<Hole> holes) {
		return new HoleStream(holes);
	}

	default Integer getStablefordPoints() {
		//TODO varying stableford point methods, i.e. negative on doubles and net considerations
		int diff = getPar() - getStrokes() + 1;
		if (diff < 0) return 0;
		if (diff == 0) return 1;
		return (int) Math.pow(2,diff);
	}
}
