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
	private final BigDecimal strokesGained;

	public HoleAnalysis(Hole hole) {
		this.number = hole.getNumber();
		this.par = hole.getPar();
		this.strokes = hole.getStrokes();
		this.strokesAdjusted = hole.getStrokesAdjusted();
		this.score = hole.getAdjustedScore();
		this.yards = hole.getYards();
		this.strokesGained = hole.getStrokesGained();
	}
}
