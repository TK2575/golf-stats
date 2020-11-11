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
import java.util.List;

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

	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;
	private final Integer netScore;

	private final boolean nineHoleRound;

	@Getter(AccessLevel.NONE) @ToString.Exclude
	private final Collection<Hole> holes;

	public HoleByHoleRound(IncompleteRound round, Collection<Hole> holes) {
		this.date = round.getDate();
		this.duration = round.getDuration();
		this.golfer = round.getGolfer();
		this.course = round.getCourse();
		this.transport = round.getTransport();

		List<Hole> validatedHoles = Hole.stream(holes).validate().asList();
		this.tee = Tee.of(this.golfer, round.getTeeName(), round.getRating(), round.getSlope(), Hole.stream(validatedHoles).getPar());
		this.holes = Hole.stream(validatedHoles).applyNetDoubleBogey(this.tee, this.golfer).sortFirstToLast().asList();

		this.strokes = holes().totalStrokes();
		this.strokesAdjusted = holes().totalStrokesAdjusted();
		this.netScore = holes().totalNetStrokes();
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
