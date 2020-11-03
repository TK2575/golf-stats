package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.IncompleteRound;
import dev.tk2575.golfstats.golfround.Transport;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

@Getter
@ToString
public class HoleByHoleRound implements GolfRound {
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

	@Getter(AccessLevel.NONE) @ToString.Exclude
	private final Collection<Hole> holes;

	public HoleByHoleRound(IncompleteRound round, Collection<Hole> holes) {
		this.holes = Hole.stream(holes).validate().sortFirstToLast().asList();

		this.date = round.getDate();
		this.duration = round.getDuration();
		this.golfer = round.getGolfer();
		this.course = round.getCourse();
		this.tee = Tee.of(round.getTeeName(), round.getRating(), round.getSlope(), holes().getPar());
		this.transport = round.getTransport();

		this.score = holes().totalStrokes();
		this.fairwaysInRegulation = holes().totalFairwaysInRegulation();
		this.fairways = holes().totalFairways();
		this.greensInRegulation = holes().totalGreensInRegulation();
		this.putts = holes().totalPutts();
		this.nineHoleRound = holes().isNineHoleRound();

		this.scoreDifferential = computeScoreDifferential();
	}

	@Override
	public HoleStream getHoles() {
		return holes();
	}

	public Integer getHoleCount() {
		return this.holes.size();
	}

	private HoleStream holes() {
		return Hole.stream(this.holes);
	}
}
