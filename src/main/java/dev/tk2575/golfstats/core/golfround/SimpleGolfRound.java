package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@ToString
class SimpleGolfRound implements GolfRound {

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

	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;

	private boolean nineHoleRound;

	SimpleGolfRound(LocalDate date, Duration duration, Golfer golfer, Course course, Tee tee, Transport transport, Integer strokes, Integer strokesAdjusted, Integer fairwaysInRegulation, Integer fairways, Integer greensInRegulation, Integer putts, boolean nineHoleRound) {
		//TODO add null checks
		this.date = date;
		this.duration = duration;
		this.golfer = golfer;
		this.course = course;
		this.tee = tee;
		this.transport = transport;
		this.strokes = strokes;
		this.strokesAdjusted = strokesAdjusted;
		this.fairwaysInRegulation = fairwaysInRegulation;
		this.fairways = fairways;
		this.greensInRegulation = greensInRegulation;
		this.putts = putts;
		this.nineHoleRound = nineHoleRound;
		this.scoreDifferential = computeScoreDifferential();
	}
}
