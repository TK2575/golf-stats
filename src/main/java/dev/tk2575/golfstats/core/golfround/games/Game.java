package dev.tk2575.golfstats.core.golfround.games;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
public abstract class Game implements GolfRound {
	//TODO separate from GolfRound
	private final GolfRound round;
	private BigDecimal incomingHandicapIndex;

	@Override
	public Integer getNetStrokes() {
		return round.getNetStrokes();
	}

	protected Game(GolfRound round) {
		if (round == null) {
			throw new IllegalArgumentException("round cannot be null");
		}
		this.round = round;
	}

	public static Game stablefordAllPositive(GolfRound round) {
		if (round.getHoles().isEmpty()) {
			return new EmptyScore(round);
		}
		return new StablefordAllPositive(round);
	}

	public boolean isScoreComputed() {
		return true;
	}

	public abstract int score(Hole h);

	@Override
	public GolfRound applyNetDoubleBogey(BigDecimal incomingIndex) {
		this.incomingHandicapIndex = incomingIndex;
		return this;
	}

	@Override
	public LocalDate getDate() {
		return round.getDate();
	}

	@Override
	public Duration getDuration() {
		return round.getDuration();
	}

	@Override
	public Golfer getGolfer() {
		return round.getGolfer();
	}

	@Override
	public Course getCourse() {
		return round.getCourse();
	}

	@Override
	public Tee getTee() {
		return round.getTee();
	}

	@Override
	public String getTransport() {
		return round.getTransport();
	}

	@Override
	public BigDecimal getScoreDifferential() {
		return round.getScoreDifferential();
	}

	@Override
	public Integer getStrokes() {
		return round.getStrokes();
	}

	@Override
	public Integer getStrokesAdjusted() {
		return round.getStrokesAdjusted();
	}

	@Override
	public Integer getFairwaysInRegulation() {
		return round.getFairwaysInRegulation();
	}

	@Override
	public Integer getFairways() {
		return round.getFairways();
	}

	@Override
	public Integer getGreensInRegulation() {
		return round.getGreensInRegulation();
	}

	@Override
	public Integer getPutts() {
		return round.getPutts();
	}

	@Override
	public Integer getHoleCount() {
		return round.getHoleCount();
	}

	@Override
	public boolean isNineHoleRound() {
		return round.isNineHoleRound();
	}
}
