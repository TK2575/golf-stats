package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.course.tee.Tee;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
public class SimpleGolfRound implements GolfRound {

	@Getter(AccessLevel.NONE)
	private static final Integer HOLES = 18;

	public Integer getHoleCount() {
		return HOLES;
	}

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final Transport transport;

	private final BigDecimal scoreDifferential;

	private final Integer score;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;

	private final boolean nineHoleRound;

	public SimpleGolfRound(SimpleGolfRoundCSVParser factory) {
		this.date = factory.getDate();
		this.golfer = Golfer.newGolfer(factory.getGolferName());
		this.course = Course.of(factory.getCourseName());
		this.tee = Tee.of(factory.getTees(), factory.getRating(), factory.getSlope(), factory.getPar());
		this.duration = factory.getDuration();
		this.transport = factory.getTransport();
		this.score = factory.getScore();
		this.fairwaysInRegulation = factory.getFairwaysInRegulation();
		this.fairways = factory.getFairways();
		this.greensInRegulation = factory.getGreensInRegulation();
		this.putts = factory.getPutts();
		this.scoreDifferential = computeScoreDifferential();
		this.nineHoleRound = factory.getNineHoleRound();
	}
}
