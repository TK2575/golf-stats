package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString
public class SimpleCompositeGolfRound implements GolfRound {

	private static final String DELIMITER = " - ";

	@Getter(AccessLevel.NONE)
	private static final Integer HOLES = 18;

	@Override
	public BigDecimal getRating() {
		return this.tee.getRating();
	}

	@Override
	public BigDecimal getSlope() {
		return this.tee.getSlope();
	}

	public Integer getHoleCount() {
		return HOLES;
	}

	public boolean isNineHoleRound() {
		return false;
	}

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final Transport transport;

	private final BigDecimal scoreDifferential;

	private final Integer par;
	private final Integer score;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;

	@ToString.Exclude
	private GolfRound firstRound;
	@ToString.Exclude
	private GolfRound secondRound;

	public SimpleCompositeGolfRound(GolfRound round1, GolfRound round2) {
		validateArguments(round1, round2);
		assignFirstAndSecondRound(round1, round2);

		this.date = secondRound.getDate();
		this.golfer = secondRound.getGolfer();

		this.course = Course.compositeOf(firstRound.getCourse(), secondRound.getCourse());
		this.tee = Tee.compositeOf(firstRound.getTee(), secondRound.getTee());
		this.transport = Transport.compositeOf(firstRound.getTransport(), secondRound.getTransport());

		this.duration = setDuration(firstRound.getDuration(), secondRound.getDuration());

		this.par = firstRound.getPar() + secondRound.getPar();
		this.score = firstRound.getScore() + secondRound.getScore();
		this.fairwaysInRegulation = firstRound.getFairwaysInRegulation() + secondRound.getFairwaysInRegulation();
		this.fairways = firstRound.getFairways() + secondRound.getFairways();
		this.greensInRegulation = firstRound.getGreensInRegulation() + secondRound.getGreensInRegulation();
		this.putts = firstRound.getPutts() + secondRound.getPutts();

		this.scoreDifferential = computeScoreDifferential();
	}

	private Duration setDuration(Duration duration1, Duration duration2) {
		if (duration1 == null) { return duration2; }
		else if (duration2 == null) { return duration1; }
		else { return duration1.plus(duration2); }
	}

	private void assignFirstAndSecondRound(GolfRound round1, GolfRound round2) {
		if (round1.getDate().isBefore(round2.getDate())) {
			this.firstRound = round1;
			this.secondRound = round2;
		}
		else {
			this.firstRound = round2;
			this.secondRound = round1;
		}
	}

	private void validateArguments(GolfRound round1, GolfRound round2) {
		if (round1 == null || round2 == null) {
			throw new IllegalArgumentException("round1 and round2 are required arguments");
		}

		if (round1.equals(round2)) {
			throw new IllegalArgumentException("these rounds are the same");
		}

		if (!round1.isNineHoleRound() || !round2.isNineHoleRound()) {
			throw new IllegalArgumentException("both rounds must be nine hole rounds");
		}

		if (!round1.getGolfer().equals(round2.getGolfer())) {
			throw new IllegalArgumentException("cannot create composite round for two different golfers' rounds");
		}
	}
}
