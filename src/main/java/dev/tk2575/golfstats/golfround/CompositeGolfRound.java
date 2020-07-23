package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Utils;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
public class CompositeGolfRound implements GolfRound {

	private static final String DELIMITER = " - ";

	@Getter(AccessLevel.NONE) private static final Integer HOLES = 18;

	public Integer getHoles() {
		return HOLES;
	}

	private LocalDate date;
	private String golfer;
	private String course;
	private String tees;
	private Duration duration;
	private String transport;
	private BigDecimal rating;
	private BigDecimal slope;
	private Integer par;
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;

	private NineHoleRound firstRound;
	private NineHoleRound secondRound;

	public CompositeGolfRound(NineHoleRound round1, NineHoleRound round2) {
		validateArguments(round1, round2);
		assignFirstAndSecondRound(round1, round2);

		this.date = secondRound.getDate();
		this.golfer = secondRound.getGolfer();

		this.course = secondRound.getCourse();
		if (!this.course.equalsIgnoreCase(firstRound.getCourse())) {
			this.course = String.join(DELIMITER, firstRound.getCourse(), secondRound.getCourse());
		}

		this.tees = secondRound.getTees();
		if (!this.tees.equalsIgnoreCase(firstRound.getTees())) {
			this.tees = String.join(DELIMITER, firstRound.getTees(), secondRound.getTees());
		}

		this.transport = secondRound.getTransport();
		if (!this.transport.equalsIgnoreCase(firstRound.getTees())) {
			this.transport = String.join(DELIMITER, firstRound.getTransport(), secondRound.getTransport());
		}

		this.duration = firstRound.getDuration().plus(secondRound.getDuration());
		this.rating = firstRound.getRating().add(secondRound.getRating());
		this.slope = Utils.mean(firstRound.getSlope(), secondRound.getSlope());

		this.par = firstRound.getPar() + secondRound.getPar();
		this.score = firstRound.getScore() + secondRound.getScore();
		this.fairwaysInRegulation = firstRound.getFairwaysInRegulation() + secondRound.getFairwaysInRegulation();
		this.fairways = firstRound.getFairways() + secondRound.getFairways();
		this.greensInRegulation = firstRound.getGreensInRegulation() + secondRound.getGreensInRegulation();
		this.putts = firstRound.getPutts() + secondRound.getPutts();
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

		if (!round1.getGolfer().equalsIgnoreCase(round2.getGolfer())) {
			throw new IllegalArgumentException("cannot create composite round for two different golfers' rounds");
		}
	}
}
