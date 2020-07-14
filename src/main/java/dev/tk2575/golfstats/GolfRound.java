package dev.tk2575.golfstats;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;

@Data
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
public class GolfRound {

	private static final int SCALE = 2;
	private static final RoundingMode HALF_UP = RoundingMode.HALF_UP;

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private LocalDate date;
	private String golfer;
	private String course;
	private String tees;
	private Duration duration;
	private String transport;
	private BigDecimal rating; //put whatever's on the scorecard, adjust if it's way off of par
	private BigDecimal slope;
	private Integer par; //expect par of holes played to be inputted (i.e. put 36 if playing a nine hole round)
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;
	private Boolean nineHoleRound;

	@Setter(AccessLevel.NONE) private Integer holes;
	@Setter(AccessLevel.NONE) private BigDecimal scoreDifferential;
	@Setter(AccessLevel.NONE) private BigDecimal puttsPerHole;
	@Setter(AccessLevel.NONE) private BigDecimal effectiveCourseRating;
	@Setter(AccessLevel.NONE) private BigDecimal fairwayInRegulationRate;
	@Setter(AccessLevel.NONE) private BigDecimal greenInRegulationRate;
	@Setter(AccessLevel.NONE) private BigDecimal minutesPerHole;
	@Setter(AccessLevel.NONE) private Integer scoreToPar; //overUnder

	public GolfRound(String golfer, String[] row) {
		this.golfer = golfer;
		this.date = LocalDate.parse(row[0], DATE_FORMAT);
		this.course = row[1];
		this.tees = row[2];
		this.rating = new BigDecimal(row[3]);
		this.slope = new BigDecimal(row[4]);
		this.par = Integer.valueOf(row[5]);
		this.duration = row[6] == null || row[6].isBlank() ? null : Duration.between(LocalTime.MIN, LocalTime.parse(row[6], DURATION_FORMAT));
		this.transport = row[7];
		this.score = Integer.valueOf(row[8]);
		this.fairwaysInRegulation = Integer.valueOf(row[9]);
		this.fairways = Integer.valueOf(row[10]);
		this.greensInRegulation = Integer.valueOf(row[11]);
		this.putts = Integer.valueOf(row[12]);
		this.nineHoleRound = Boolean.parseBoolean(row[13]);
		calculate();
	}

	private void calculate() {
		this.holes = Boolean.TRUE.equals(nineHoleRound) ? 9 : 18;
		this.puttsPerHole = divideInts(putts, holes);
		this.fairwayInRegulationRate = divideInts(fairwaysInRegulation, fairways);
		this.greenInRegulationRate = divideInts(greensInRegulation, holes);
		this.scoreToPar = score - par;
		this.effectiveCourseRating = correctCourseRating(par, rating);
		this.scoreDifferential = computeScoreDifferential();
		this.minutesPerHole = duration == null ? BigDecimal.ZERO : divideInts(Math.toIntExact(duration.toMinutes()), holes);
	}

	private BigDecimal computeScoreDifferential() {
		BigDecimal firstTerm = BigDecimal.valueOf(113).setScale(2).divide(slope, HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(score).subtract(effectiveCourseRating).setScale(2);
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
