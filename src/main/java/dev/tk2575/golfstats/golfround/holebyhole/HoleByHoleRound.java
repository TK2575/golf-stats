package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.golfround.Course;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.Tee;
import dev.tk2575.golfstats.golfround.Transport;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
public class HoleByHoleRound implements GolfRound, HoleByHole {
	private LocalDate date;
	private Duration duration;

	private Golfer golfer;
	private Course course;
	private Tee tee;
	private Transport transport;

	private BigDecimal scoreDifferential;

	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;

	private boolean nineHoleRound;

	@Getter(AccessLevel.NONE) @ToString.Exclude
	private List<Hole> holes;

	public HoleByHoleRound(HoleByHoleRoundCSVParser factory) {
		this.date = factory.getDate();
		this.duration = factory.getDuration();
		this.golfer = Golfer.newGolfer(factory.getGolfer());
		this.course = Course.newCourse(factory.getCourse());
		this.tee = Tee.newTee(factory.getTees(), factory.getRating(), factory.getSlope(), factory.getPar());
		this.transport = Transport.valueOf(factory.getTransport());

		this.holes = Hole.stream(factory.getHoles()).validate().sortFirstToLast().asList();

		this.score = holes().totalStrokes();
		this.fairwaysInRegulation = holes().totalFairwaysInRegulation();
		this.fairways = holes().totalFairways();
		this.greensInRegulation = holes().totalGreensInRegulation();
		this.putts = holes().totalPutts();
		this.nineHoleRound = holes().isNineHoleRound();

		this.scoreDifferential = computeScoreDifferential();
	}

	public HoleStream getHoles() {
		return Hole.stream(this.holes);
	}

	public Integer getHoleCount() {
		return this.holes.size();
	}

	private HoleStream holes() {
		return Hole.stream(this.holes);
	}
}
