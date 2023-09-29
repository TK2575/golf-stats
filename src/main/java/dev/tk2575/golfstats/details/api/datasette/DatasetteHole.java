package dev.tk2575.golfstats.details.api.datasette;

import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
class DatasetteHole {
    
    private final int roundId;
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

    private DatasetteHole(int roundId, Hole hole) {
        this.roundId = roundId;
        this.number = hole.getNumber();
        this.par = hole.getPar();
        this.strokes = hole.getStrokes();
        this.strokesAdjusted = hole.getStrokesAdjusted();
        this.score = hole.getScore();
        this.yards = hole.getYards();
        this.netStrokes = hole.getNetStrokes();
        this.netScore = hole.getNetScore();
        this.strokesGained = hole.getStrokesGained();
        this.fairwayInRegulation = hole.isFairwayInRegulation();
        this.greenInRegulation = hole.isGreenInRegulation();
        this.putts = hole.getPutts();
    }

    static List<DatasetteHole> compile(int roundId, List<Hole> holes) {
       return new HoleStream(holes).map(hole -> new DatasetteHole(roundId, hole)).toList();
    }
}
