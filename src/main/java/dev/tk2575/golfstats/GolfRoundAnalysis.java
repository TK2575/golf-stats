package dev.tk2575.golfstats;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Getter
@ToString
public class GolfRoundAnalysis {

	private static final int SCALE = 2;
	private static final RoundingMode HALF_UP = RoundingMode.HALF_UP;

	private GolfRound data;
	private Integer holes;
	private BigDecimal scoreDifferential;
	private BigDecimal puttsPerHole;
	private BigDecimal effectiveCourseRating;
	private BigDecimal fairwayInRegulationRate;
	private BigDecimal greenInRegulationRate;
	private Duration durationPerHole;
	private Integer scoreToPar; //overUnder

	public GolfRoundAnalysis(GolfRound round) {
		this.data = round;
		this.holes = Boolean.TRUE.equals(data.getNineHoleRound()) ? 9 : 18;
		this.puttsPerHole = divideInts(data.getPutts(), holes);
		this.fairwayInRegulationRate = divideInts(data.getFairwaysInRegulation(), data.getFairways());
		this.greenInRegulationRate = divideInts(data.getGreensInRegulation(), holes);
		this.scoreToPar = data.getScore() - data.getPar();
		this.effectiveCourseRating = correctCourseRating(data.getPar(), data.getRating());
		this.scoreDifferential = computeScoreDifferential();
		//TODO duration per hole
	}

	private BigDecimal computeScoreDifferential() {
		BigDecimal firstTerm = BigDecimal.valueOf(113).setScale(2).divide(data.getSlope(), HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(data.getScore()).subtract(effectiveCourseRating).setScale(2);
		return firstTerm.multiply(secondTerm).setScale(2, HALF_UP);
	}

	private BigDecimal correctCourseRating(Integer par, BigDecimal rating) {
		if (rating.subtract(BigDecimal.valueOf(par)).compareTo(BigDecimal.TEN) > 0) {
			return rating.divide(new BigDecimal("2"), HALF_UP).setScale(SCALE, HALF_UP);
		}
		else {
			return rating;
		}
	}

	private static BigDecimal divideInts(Integer value, Integer divisor) {
		return BigDecimal.valueOf((float) value / divisor).setScale(SCALE, HALF_UP);
	}
}
