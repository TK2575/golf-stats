package dev.tk2575.golfstats.details.imports;

import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.*;

@ToString
public class Hole19Score {
	private Hole19Hole hole;
	private Integer totalOfStrokes;
	private Integer totalOfPutts;
	private Integer totalOfSandShots;
	private Integer totalOfPenalties;
	private String fairwayHit;
	private boolean greenInRegulation;
	private boolean scrambling;
	private boolean sandSaves;
	private boolean scratched;
	private boolean upAndDown;
	private boolean possibleUpAndDown;

	public boolean isFairwayHit() {
		return this.fairwayHit != null && this.fairwayHit.equalsIgnoreCase("target");
	}

	public Hole convert() {
		return Hole.of(hole.getSequence(), hole.getStrokeIndex(), hole.getPar(), totalOfStrokes, isFairwayHit(), totalOfPutts);
	}
}
