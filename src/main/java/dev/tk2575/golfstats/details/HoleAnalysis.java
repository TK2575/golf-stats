package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.*;

@Getter
public class HoleAnalysis {

	private final int number;
	private final int par;
	private final int score;
	private final long yards;

	public HoleAnalysis(Hole hole) {
		this.number = hole.getNumber();
		this.par = hole.getPar();
		this.score = hole.getStrokesAdjusted();
		this.yards = hole.getYards();
	}
}
