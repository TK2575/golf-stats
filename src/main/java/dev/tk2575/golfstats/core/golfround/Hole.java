package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import dev.tk2575.golfstats.core.strokesgained.ShotsGainedComputation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface Hole {

	static HoleStream stream(Collection<Hole> holes) {
		return new HoleStream(holes);
	}

	static Hole of(Integer number, Integer index, Integer par, List<Shot> shots) {
		return new ShotByShotHole(number, index, par, shots, ShotsGainedComputation.broadie());
	}

	static Hole of(Integer number, Integer index, Integer par, Integer strokes, boolean fairwayInRegulation, Integer putts) {
		return new SimpleHoleScore(number, index, par, strokes, fairwayInRegulation, putts);
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

	default Integer getScore() {
		return getStrokes() - getPar();
	}

	default Integer getAdjustedScore() {
		return getStrokesAdjusted() - getPar();
	}

	default boolean isFairwayPresent() {
		return getPar() > 3;
	}

	default boolean isGreenInRegulation() {
		return getPar() - 2 >= getStrokes() - getPutts();
	}

	default BigDecimal getStrokesGainedBaseline() {
		return getShots().teeShotStrokesGainedBaseline();
	}

	default BigDecimal getStrokesGained() {
		return getShots().totalStrokesGained();
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

	default Long getYards() {
		return getShots().yardsFromTee();
	}
}
