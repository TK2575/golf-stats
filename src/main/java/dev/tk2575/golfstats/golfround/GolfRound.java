package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import static java.math.RoundingMode.HALF_UP;

public interface GolfRound {

	LocalDate getDate();
	String getGolfer();
	String getCourse();
	String getTees();
	Duration getDuration();
	String getTransport();
	BigDecimal getRating();
	BigDecimal getSlope();
	Integer getPar();
	Integer getScore();
	Integer getFairwaysInRegulation();
	Integer getFairways();
	Integer getGreensInRegulation();
	Integer getPutts();
	Integer getHoles();

	default BigDecimal getScoreDifferential() {
		BigDecimal firstTerm = BigDecimal.valueOf(113).setScale(2, HALF_UP).divide(getSlope(), HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(getScore()).subtract(getRating()).setScale(2, HALF_UP);
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
		return getDuration() == null ? BigDecimal.ZERO : Utils.divideInts(Math.toIntExact(getDuration().toMinutes()), getHoles());
	}

	default Integer getScoreToPar() {
		return getScore() - getPar();
	}


}
