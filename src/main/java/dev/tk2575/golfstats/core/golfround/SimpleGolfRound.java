package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString
class SimpleGolfRound implements GolfRound {

	@Getter(AccessLevel.NONE)
	private static final Integer HOLES = 18;

	@Override
	public GolfRound applyNetDoubleBogey(BigDecimal incomingIndex) {
		return this;
	}

	public Integer getHoleCount() {
		return HOLES;
	}

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final String transport;

	private final BigDecimal scoreDifferential;

	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;

	private final boolean nineHoleRound;

	SimpleGolfRound(@NonNull RoundMeta meta, Integer par, Integer strokes, Integer fairwaysInRegulation, Integer fairways, Integer greensInRegulation, Integer putts, boolean nineHoleRound) {
		this.date = meta.getDate();
		this.duration = meta.getDuration();
		this.golfer = meta.getGolfer();
		this.course = meta.getCourse();
		this.tee = Tee.of(meta.getTeeName(), meta.getRating(), meta.getSlope(), par);
		this.transport = meta.getTransport();
		this.strokes = strokes;
		this.strokesAdjusted = this.strokes;
		this.fairwaysInRegulation = fairwaysInRegulation;
		this.fairways = fairways;
		this.greensInRegulation = greensInRegulation;
		this.putts = putts;
		this.nineHoleRound = nineHoleRound;
		this.scoreDifferential = computeScoreDifferential();
	}
}
