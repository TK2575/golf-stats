package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

public interface GolfRound {

	LocalDate getDate();

	Duration getDuration();

	Golfer getGolfer();

	Course getCourse();

	Tee getTee();

	Transport getTransport();

	BigDecimal getScoreDifferential();

	Integer getStrokes();

	Integer getStrokesAdjusted();

	Integer getFairwaysInRegulation();

	Integer getFairways();

	Integer getGreensInRegulation();

	Integer getPutts();

	Integer getHoleCount();

	boolean isNineHoleRound();

	default HoleStream getHoles() {
		return HoleStream.empty();
	}

	default BigDecimal computeScoreDifferential() {
		BigDecimal firstTerm = BigDecimal.valueOf(113)
		                                 .divide(getSlope(), 2, HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(getStrokesAdjusted())
		                                  .subtract(getRating())
		                                  .setScale(2, HALF_UP);
		return firstTerm.multiply(secondTerm);
	}

	default Integer getHandicapStrokes() {
		Tee handicap = getTee().handicapOf(getGolfer());
		return handicap.getHandicapStrokesForGolfer(getGolfer());
	}

	default Integer getNetScore() {
		return getStrokes() - getHandicapStrokes();
	}

	default BigDecimal getPuttsPerHole() {
		return Utils.divideInts(getPutts(), getHoleCount());
	}

	default BigDecimal getFairwayInRegulationRate() {
		return Utils.divideInts(getFairwaysInRegulation(), getFairways());
	}

	default BigDecimal getGreensInRegulationRate() {
		return Utils.divideInts(getGreensInRegulation(), getHoleCount());
	}

	default BigDecimal getMinutesPerHole() {
		return getDuration() == null
		       ? BigDecimal.ZERO
		       : Utils.divideInts(Math.toIntExact(getDuration().toMinutes()), getHoleCount());
	}

	default BigDecimal getRating() {
		return getTee().getRating();
	}

	default BigDecimal getSlope() {
		return getTee().getSlope();
	}

	default Integer getPar() {
		return getTee().getPar();
	}

	default Integer getScore() { return getStrokes() - getPar(); }

	default Integer getScoreToPar() {
		return getStrokes() - getPar();
	}

	default Long getYards() { return getTee().getYards(); }

	default BigDecimal getStrokesGained() { return BigDecimal.ZERO; }

	static GolfRound compositeOf(GolfRound round1, GolfRound round2) {
		if (round1.getHoles().isEmpty() || round2.getHoles().isEmpty()) {
			return new SimpleCompositeGolfRound(round1, round2);
		}
		return new CompositeHoleByHoleRound(round1, round2);
	}

	static GolfRoundStream stream(Collection<GolfRound> rounds) {
		return new GolfRoundStream(rounds.stream());
	}

	static List<GolfRound> compile(Map<Integer, IncompleteRound> roundDetails, Map<Integer, List<Hole>> holes) {
		List<GolfRound> results = new ArrayList<>();
		HandicapIndex index;
		IncompleteRound incompleteRound;

		//TODO refactor without for loop?
		for (Map.Entry<Integer, IncompleteRound> each : roundDetails.entrySet()) {
			incompleteRound = each.getValue();
			if (!results.isEmpty()) {
				index = HandicapIndex.newIndex(results);
				incompleteRound = new IncompleteRound(incompleteRound, index);
			}
			results.add(of(incompleteRound, holes.get(each.getKey())));
		}
		return results;
	}

	static GolfRound of(IncompleteRound round, List<Hole> holes) {
		return new HoleByHoleRound(round, holes);
	}

	static GolfRound of(LocalDate date, Duration duration, Golfer golfer, Course course, Tee tee, Transport transport, Integer strokes, Integer strokesAdjusted, Integer fairwaysInRegulation, Integer fairways, Integer greensInRegulation, Integer putts, boolean nineHoleRound) {
		return new SimpleGolfRound(date, duration, golfer, course, tee, transport, strokes, strokesAdjusted, fairwaysInRegulation, fairways, greensInRegulation, putts, nineHoleRound);
	}

}
