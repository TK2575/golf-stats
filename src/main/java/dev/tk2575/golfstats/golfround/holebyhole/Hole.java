package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.golfround.shotbyshot.ShotStream;

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

	default ShotStream getShots() { return ShotStream.empty(); }

	default boolean isFairwayPresent() {
		return getPar() > 3;
	}

	default boolean isGreenInRegulation() {
		return getPar() - 2 >= getStrokes() - getPutts();
	}

	default Integer computeNetDoubleBogey(Integer handicapStrokes) {
		int strokesOnAllHoles = handicapStrokes / 18;
		int strokeOnThisHole = 0;
		if (handicapStrokes >= 0 && handicapStrokes % 18 >= getIndex()) {
			strokeOnThisHole = 1;
		}
		else if (handicapStrokes < 0 && handicapStrokes % 18 <= getIndex()) {
			strokeOnThisHole = -1;
		}

		return strokesOnAllHoles + strokeOnThisHole;
	}
}
