package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
class SimpleCompositeGolfRound implements GolfRound {

	private static final String DELIMITER = " - ";

	@Getter(AccessLevel.NONE)
	private static final Integer HOLES = 18;

	@Override
	public BigDecimal getRating() {
		return this.tee.getRating();
	}

	@Override
	public BigDecimal getSlope() {
		return this.tee.getSlope();
	}

	@Override
	public GolfRound applyNetDoubleBogey(BigDecimal incomingIndex) {
		return this.toBuilder()
				.netStrokes(this.strokesAdjusted - this.tee.handicapStrokes(incomingIndex))
				.incomingHandicapIndex(incomingIndex)
				.build();
	}

	public Integer getHoleCount() {
		return HOLES;
	}

	public boolean isNineHoleRound() {
		return false;
	}

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final String transport;

	private BigDecimal incomingHandicapIndex;
	private final BigDecimal scoreDifferential;

	private final Integer par;
	private final Integer strokes;
	private final Integer strokesAdjusted;
	private final Integer netStrokes;
	private final Integer fairwaysInRegulation;
	private final Integer fairways;
	private final Integer greensInRegulation;
	private final Integer putts;

	@ToString.Exclude
	private final GolfRound firstRound;
	@ToString.Exclude
	private final GolfRound secondRound;

	SimpleCompositeGolfRound(GolfRound round1, GolfRound round2) {
		this.firstRound = round1;
		this.secondRound = round2;

		this.date = secondRound.getDate();
		this.golfer = secondRound.getGolfer();

		this.course = Course.compositeOf(firstRound.getCourse(), secondRound.getCourse());
		this.tee = Tee.compositeOf(firstRound.getTee(), secondRound.getTee());
		this.transport =
				firstRound.getTransport().equalsIgnoreCase(secondRound.getTransport())
						? firstRound.getTransport()
						: "Various";

		this.duration = setDuration(firstRound.getDuration(), secondRound.getDuration());

		this.par = firstRound.getPar() + secondRound.getPar();
		this.strokes = firstRound.getStrokes() + secondRound.getStrokes();
		this.strokesAdjusted = this.strokes;
		this.netStrokes = this.strokes;
		this.fairwaysInRegulation = firstRound.getFairwaysInRegulation() + secondRound.getFairwaysInRegulation();
		this.fairways = firstRound.getFairways() + secondRound.getFairways();
		this.greensInRegulation = firstRound.getGreensInRegulation() + secondRound.getGreensInRegulation();
		this.putts = firstRound.getPutts() + secondRound.getPutts();

		this.scoreDifferential = computeScoreDifferential();
	}

	private Duration setDuration(Duration duration1, Duration duration2) {
		if (duration1 == null) { return duration2; }
		else if (duration2 == null) { return duration1; }
		else { return duration1.plus(duration2); }
	}
}
