package dev.tk2575.golfstats.core.golfround;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
class HoleByHoleRound implements GolfRound {
	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final String transport;

	private BigDecimal incomingHandicapIndex;
	private final BigDecimal scoreDifferential;

	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final Integer netStrokes;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;

	private final boolean nineHoleRound;

	private final BigDecimal strokesGained;
	private final Map<String, BigDecimal> strokesGainedByShotType;
	private final Map<Integer, BigDecimal> strokesGainedByHole;

	@ToString.Exclude
	@JsonIgnore
	private final Collection<Hole> holes;

	HoleByHoleRound(@NonNull RoundMeta round, @NonNull Collection<Hole> holes) {
		this.date = round.getDate();
		this.duration = round.getDuration();
		this.golfer = round.getGolfer();
		this.course = round.getCourse();
		this.transport = round.getTransport();

		this.holes = Hole.stream(holes).validate().sortFirstToLast().asList();
		this.tee = Tee.of(this.golfer, round.getTeeName(), round.getRating(), round.getSlope(), holes().getPar());

		this.strokes = holes().totalStrokes();
		this.strokesAdjusted = holes().totalStrokesAdjusted();
		this.netStrokes = holes().totalNetStrokes();
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
		//FIXME net strokes/score looks wrong for holebyhole rounds
		List<Hole> holesAdjusted = holes().applyNetDoubleBogey(this.tee.handicapStrokes(incomingIndex)).toList();
		return new HoleByHoleRound(new RoundMeta(this), holesAdjusted).toBuilder().incomingHandicapIndex(incomingIndex).build();
	}

	public Integer getHoleCount() {
		return this.holes.size();
	}

	private HoleStream holes() {
		return Hole.stream(this.holes);
	}
}
