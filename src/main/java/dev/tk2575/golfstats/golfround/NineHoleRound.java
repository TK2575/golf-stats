package dev.tk2575.golfstats.golfround;

import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import static java.math.RoundingMode.HALF_UP;

@Getter
public class NineHoleRound implements GolfRound {

	@Getter(AccessLevel.NONE) private static final Integer HOLES = 9;

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

	public NineHoleRound(GolfRoundFactory factory) {
		this.date = factory.getDate();
		this.golfer = factory.getGolfer();
		this.course = factory.getCourse();
		this.tees = factory.getTees();
		this.duration = factory.getDuration();
		this.transport = factory.getTransport();
		this.rating = correctCourseRating(factory.getPar(), factory.getRating());
		this.slope = factory.getSlope();
		this.par = factory.getPar();
		this.score = factory.getScore();
		this.fairwaysInRegulation = factory.getFairwaysInRegulation();
		this.fairways = factory.getFairways();
		this.greensInRegulation = factory.getGreensInRegulation();
		this.putts = factory.getPutts();
	}

	private BigDecimal correctCourseRating(Integer par, BigDecimal rating) {
		if (rating.subtract(BigDecimal.valueOf(par)).compareTo(BigDecimal.TEN) > 0) {
			return rating.divide(new BigDecimal("2"), HALF_UP).setScale(2, HALF_UP);
		}
		else {
			return rating;
		}
	}
}
