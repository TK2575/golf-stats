package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString
public class CompositeGolfRound implements GolfRound {

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

	public Integer getHoles() {
		return HOLES;
	}

	private LocalDate date;
	private Duration duration;

	private Golfer golfer;
	private Course course;
	private Tee tee;
	private String transport;

	private BigDecimal scoreDifferential;

	private Integer par;
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;

	@ToString.Exclude
	private NineHoleRound firstRound;
	@ToString.Exclude
	private NineHoleRound secondRound;

	public CompositeGolfRound(NineHoleRound round1, NineHoleRound round2) {
		//TODO better defend against individual fields being null

		validateArguments(round1, round2);
		assignFirstAndSecondRound(round1, round2);

		this.date = secondRound.getDate();
		this.golfer = secondRound.getGolfer();

		this.course = Course.compositeCourse(firstRound.getCourse(), secondRound
				.getCourse());

		this.tee = Tee.compositeTee(firstRound.getTee(), secondRound.getTee());

		this.transport = secondRound.getTransport();
		if (!this.transport.equalsIgnoreCase(firstRound.getTransport())) {
			this.transport = String.join(DELIMITER, firstRound.getTransport(), secondRound
					.getTransport());
		}

		this.duration = setDuration(firstRound.getDuration(), secondRound.getDuration());

		this.par = firstRound.getPar() + secondRound.getPar();
		this.score = firstRound.getScore() + secondRound.getScore();
		this.fairwaysInRegulation = firstRound.getFairwaysInRegulation() + secondRound
				.getFairwaysInRegulation();
		this.fairways = firstRound.getFairways() + secondRound.getFairways();
		this.greensInRegulation = firstRound.getGreensInRegulation() + secondRound
				.getGreensInRegulation();
		this.putts = firstRound.getPutts() + secondRound.getPutts();

		this.scoreDifferential = computeScoreDifferential();
	}

	private Duration setDuration(Duration duration1, Duration duration2) {
		if (duration1 == null) { return duration2; }
		else if (duration2 == null) { return duration1; }
		else { return duration1.plus(duration2); }
	}

	private void assignFirstAndSecondRound(NineHoleRound round1, NineHoleRound round2) {
		if (round1.getDate().isBefore(round2.getDate())) {
			this.firstRound = round1;
			this.secondRound = round2;
		}
		else {
			this.firstRound = round2;
			this.secondRound = round1;
		}
	}

	private void validateArguments(NineHoleRound round1, NineHoleRound round2) {
		if (round1 == null || round2 == null) {
			throw new IllegalArgumentException("round1 and round2 are required arguments");
		}

		if (round1.equals(round2)) {
			throw new IllegalArgumentException("these nine hole rounds are the same");
		}

		if (!round1.getGolfer().equals(round2.getGolfer())) {
			throw new IllegalArgumentException("cannot create composite round for two different golfers' rounds");
		}
	}
}
