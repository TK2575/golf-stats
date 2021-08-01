package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class RoundSummary {
	private final int roundId;
	private final LocalDate date;
	private final String course;
	private final String location;
	private final int strokes;
	private final int score;
	private final int strokesAdjusted;
	private final int netStrokes;
	private final int netScore;
	private final int par;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final BigDecimal incomingHandicapIndex;
	private final BigDecimal handicapDifferential;
	private final String transport;

	public String getDisplay() {
		return String.join(" - ", this.date.format(DateTimeFormatter.BASIC_ISO_DATE), this.course);
	}

	public RoundSummary(int number, GolfRound round) {
		this.roundId = number;
		this.date = round.getDate();
		this.course = round.getCourse().getName();
		this.location = round.getCourse().getLocation();
		this.strokes = round.getStrokes();
		this.strokesAdjusted = round.getStrokesAdjusted();
		this.score = round.getScore();
		this.par = round.getPar();
		this.netStrokes = round.getNetStrokes();
		this.netScore = round.getNetScore();
		this.rating = round.getRating();
		this.slope = round.getSlope();
		this.incomingHandicapIndex = round.getIncomingHandicapIndex();
		this.handicapDifferential = round.getScoreDifferential();
		this.transport = round.getTransport();
	}

	public RoundSummary(RoundSummary other) {
		this.roundId = other.roundId;
		this.date = other.date;
		this.course = other.course;
		this.location = other.location;
		this.strokes = other.strokes;
		this.score = other.score;
		this.strokesAdjusted = other.strokesAdjusted;
		this.netStrokes = other.netStrokes;
		this.netScore = other.netScore;
		this.par = other.par;
		this.rating = other.rating;
		this.slope = other.slope;
		this.incomingHandicapIndex = other.incomingHandicapIndex;
		this.handicapDifferential = other.handicapDifferential;
		this.transport = other.transport;
	}
}
