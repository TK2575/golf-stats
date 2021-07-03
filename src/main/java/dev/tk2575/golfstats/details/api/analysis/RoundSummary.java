package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class RoundSummary {

	private final int number;
	private final LocalDate date;
	private final String course;
	private final int strokes;
	private final int score;
	private final int netScore;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final BigDecimal handicapDifferential;
	private final List<HoleAnalysis> holes;
	private final List<ShotAnalysis> shots;
	private final String transport;

	public RoundSummary(int number, GolfRound round) {
		this.number = number;
		this.date = round.getDate();
		this.course = round.getCourse().getName();
		this.strokes = round.getStrokesAdjusted();
		this.score = round.getScore();
		this.netScore = round.getNetScore();
		this.rating = round.getRating();
		this.slope = round.getSlope();
		this.handicapDifferential = round.getScoreDifferential();
		this.holes = round.getHoles().map(HoleAnalysis::new).collect(toList());
		this.shots = round.getHoles().allShots().map(ShotAnalysis::new).collect(toList());
		this.transport = round.getTransport();
	}

}
