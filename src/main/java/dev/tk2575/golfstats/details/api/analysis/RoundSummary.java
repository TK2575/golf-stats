package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class RoundSummary {

	private final int number;
	private final LocalDate date;
	private final String course;
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
		this.number = number;
		this.date = round.getDate();
		this.course = round.getCourse().getName();
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

}
