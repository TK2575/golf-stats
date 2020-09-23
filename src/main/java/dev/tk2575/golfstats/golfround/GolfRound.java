package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.Utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

public interface GolfRound {

	LocalDate getDate();

	Duration getDuration();

	Golfer getGolfer();

	Course getCourse();

	Tee getTee();

	String getTransport();

	BigDecimal getRating();

	BigDecimal getSlope();

	BigDecimal getScoreDifferential();

	Integer getPar();

	Integer getScore();

	Integer getFairwaysInRegulation();

	Integer getFairways();

	Integer getGreensInRegulation();

	Integer getPutts();

	Integer getHoles();

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
		return Utils.divideInts(getPutts(), getHoles());
	}

	default BigDecimal getFairwayInRegulationRate() {
		return Utils.divideInts(getFairwaysInRegulation(), getFairways());
	}

	default BigDecimal getGreensInRegulationRate() {
		return Utils.divideInts(getGreensInRegulation(), getHoles());
	}

	default BigDecimal getMinutesPerHole() {
		return getDuration() == null
		       ? BigDecimal.ZERO
		       : Utils.divideInts(Math.toIntExact(getDuration().toMinutes()), getHoles());
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
				String.valueOf(getHoles())
		};
	}

	static GolfRoundStream stream(List<GolfRound> rounds) {
		return new GolfRoundStream(rounds.stream());
	}
}
