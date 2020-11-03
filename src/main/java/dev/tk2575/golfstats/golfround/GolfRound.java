package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.Utils;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.golfround.holebyhole.CompositeHoleByHoleRound;
import dev.tk2575.golfstats.golfround.holebyhole.Hole;
import dev.tk2575.golfstats.golfround.holebyhole.HoleByHoleRound;
import dev.tk2575.golfstats.golfround.holebyhole.HoleStream;

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

	Integer getScore();

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
		                                 .setScale(2, HALF_UP)
		                                 .divide(getSlope(), HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(getScore())
		                                  .subtract(getRating())
		                                  .setScale(2, HALF_UP);
		return firstTerm.multiply(secondTerm).setScale(2, HALF_UP);
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

	default Integer getScoreToPar() {
		return getScore() - getPar();
	}

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
		//TODO one-liner via stream?
		List<GolfRound> results = new ArrayList<>();
		roundDetails.forEach((k,v) -> results.add(of(v, holes.get(k))));
		return results;
	}

	static GolfRound of(IncompleteRound round, List<Hole> holes) {
		return new HoleByHoleRound(round, holes);
	}

}
