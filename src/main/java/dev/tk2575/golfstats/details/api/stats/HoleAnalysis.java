package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.*;

import java.math.BigDecimal;

@Getter
public class HoleAnalysis {

	private final int number;
	private final int par;
	private final int strokes;
	private final int strokesAdjusted;
	private final int score;
	private final long yards;
	private final int netStrokes;
	private final int netScore;
	private final BigDecimal strokesGained;
	private final boolean fairwayInRegulation;
	private final boolean greenInRegulation;
	private final int putts;

	public String getScoreName() {
		if (this.score <= -3) {
			return "Albatross";
		}
		if (this.score == -2) {
			return "Eagle";
		}
		if (this.score == -1) {
			return "Birdie";
		}
		if (this.score == 0) {
			return "Par";
		}
		if (this.score == 1) {
			return "Bogey";
		}
		if (this.score == 2) {
			return "Double Bogey";
		}
		return "Triple+ Bogey";
	}

	public HoleAnalysis(Hole hole) {
		this.number = hole.getNumber();
		this.par = hole.getPar();
		this.strokes = hole.getStrokes();
		this.strokesAdjusted = hole.getStrokesAdjusted();
		this.score = hole.getAdjustedScore();
		this.yards = hole.getYards();
		this.strokesGained = hole.getStrokesGained();
		this.netStrokes = hole.getNetStrokes();
		this.netScore = hole.getNetScore();
		this.fairwayInRegulation = hole.isFairwayInRegulation();
		this.greenInRegulation = hole.isGreenInRegulation();
		this.putts = hole.getPutts();
	}
}
