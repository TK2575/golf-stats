package dev.tk2575.golfstats.core.golfround;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@ToString
class HoleByHoleRound implements GolfRound {
	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final String transport;

	private BigDecimal scoreDifferential;

	private Integer strokes;
	private Integer strokesAdjusted;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;
	private Integer netScore;

	private boolean nineHoleRound;

	private BigDecimal strokesGained;
	private Map<String, BigDecimal> strokesGainedByShotType;
	private Map<Integer, BigDecimal> strokesGainedByHole;

	@ToString.Exclude
	@JsonIgnore
	private Collection<Hole> holes;

	HoleByHoleRound(@NonNull RoundMeta round, @NonNull Collection<Hole> holes) {
		this.date = round.getDate();
		this.duration = round.getDuration();
		this.golfer = round.getGolfer();
		this.course = round.getCourse();
		this.transport = round.getTransport();

		List<Hole> validatedHoles = Hole.stream(holes).validate().asList();
		this.tee = Tee.of(this.golfer, round.getTeeName(), round.getRating(), round.getSlope(), Hole.stream(validatedHoles).getPar());
		assignHoles(Hole.stream(validatedHoles).sortFirstToLast().asList());
	}

	private void assignHoles(Collection<Hole> holes) {
		this.holes = holes;
		this.strokes = holes().totalStrokes();
		this.strokesAdjusted = holes().totalStrokesAdjusted();
		this.netScore = holes().totalNetStrokes();
		this.fairwaysInRegulation = holes().totalFairwaysInRegulation();
		this.fairways = holes().totalFairways();
		this.greensInRegulation = holes().totalGreensInRegulation();
		this.putts = holes().totalPutts();
		this.nineHoleRound = holes().isNineHoleRound();

		this.strokesGained = holes().totalStrokesGained();
		this.strokesGainedByHole = holes().strokesGainedByHole();
		this.strokesGainedByShotType = holes().strokesGainedByShotType();

		this.scoreDifferential = computeScoreDifferential();
	}

	@Override
	public Long getYards() {
		Long yards = holes().totalYards();
		return yards > 0 ? yards : GolfRound.super.getYards();
	}

	@Override
	public HoleStream getHoles() {
		return holes();
	}

	@Override
	public GolfRound applyNetDoubleBogey(BigDecimal incomingIndex) {
		assignHoles(holes().applyNetDoubleBogey(this.tee.handicapStrokes(incomingIndex)).toList());
		return this;
	}

	public Integer getHoleCount() {
		return this.holes.size();
	}

	private HoleStream holes() {
		return Hole.stream(this.holes);
	}
}
