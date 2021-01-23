package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.Utils;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.golfround.holebyhole.CompositeHoleByHoleRound;
import dev.tk2575.golfstats.golfround.holebyhole.Hole;
import dev.tk2575.golfstats.golfround.holebyhole.HoleByHoleRound;
import dev.tk2575.golfstats.golfround.holebyhole.HoleStream;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

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

	static String[] roundHeaders() {
		return new String[]{
				"Round Type",
				"Date",
				"Course",
				"Score to Par",
				"Score Differential",
				"Fairways in Reg",
				"Fairways",
				"Greens in Reg",
				"Putts",
				"Holes"
		};
	}

	default String[] roundToArray() {
		return new String[]{
				this.getClass().getSimpleName(),
				String.valueOf(getDate()),
				getCourse().getName(),
				String.valueOf(getScoreToPar()),
				String.valueOf(getScoreDifferential()),
				String.valueOf(getFairwaysInRegulation()),
				String.valueOf(getFairways()),
				String.valueOf(getGreensInRegulation()),
				String.valueOf(getPutts()),
				String.valueOf(getHoleCount())
		};
	}

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

}
