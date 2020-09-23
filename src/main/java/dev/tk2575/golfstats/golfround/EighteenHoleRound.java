package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
public class EighteenHoleRound implements GolfRound {

	@Getter(AccessLevel.NONE) private static final Integer HOLES = 18;

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

	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;

	@Override
	public BigDecimal getRating() {
		return this.tee.getRating();
	}

	@Override
	public BigDecimal getSlope() {
		return this.tee.getSlope();
	}

	@Override
	public Integer getPar() {
		return this.tee.getPar();
	}

	public EighteenHoleRound(GolfRoundFactory factory) {
		this.date = factory.getDate();
		this.golfer = Golfer.newGolfer(factory.getGolferName());
		this.course = Course.newCourse(factory.getCourseName());
		this.tee = Tee.newTee(factory.getTees(), factory.getRating(),
				factory.getSlope(), factory
				.getPar());
		this.duration = factory.getDuration();
		this.transport = factory.getTransport();
		this.score = factory.getScore();
		this.fairwaysInRegulation = factory.getFairwaysInRegulation();
		this.fairways = factory.getFairways();
		this.greensInRegulation = factory.getGreensInRegulation();
		this.putts = factory.getPutts();
		this.scoreDifferential = computeScoreDifferential();
	}
}
