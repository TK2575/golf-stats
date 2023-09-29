package dev.tk2575.golfstats.details.api.datasette;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
class DatasetteRound {
    
    private final int roundId;
    private final String golferName;
    private final LocalDate roundDate;
    private final String courseName;
    private final String location;
    private final int strokes;
    private final int score;
    private final int strokesAdjusted;
    private final int netStrokes;
    private final int netScore;
    private final int par;
    private final BigDecimal rating;
    private final BigDecimal slope;
    private final int handicap;
    private final BigDecimal incomingHandicapIndex;
    private final String transport;
    private final int fairways;
    private final int fairwaysInRegulation;
    private final int greensInRegulation;
    private final int putts;
    private final BigDecimal strokesGained;
    private final String display;
    
    DatasetteRound(int id, GolfRound source) {
        this.roundId = id;
        this.golferName = source.getGolfer().getName();
        this.roundDate = source.getDate();
        this.courseName = source.getCourse().getName();
        this.location = source.getCourse().getLocation();
        this.strokes = source.getStrokes();
        this.score = source.getScore();
        this.strokesAdjusted = source.getStrokesAdjusted();
        this.netStrokes = source.getNetStrokes();
        this.netScore = source.getNetScore();
        this.par = source.getPar();
        this.rating = source.getRating();
        this.slope = source.getSlope();
        Integer handicapStrokesForGolfer = source.getTee().getHandicapStrokesForGolfer(source.getGolfer());
        this.handicap = handicapStrokesForGolfer == null ? 0 : handicapStrokesForGolfer;
        this.incomingHandicapIndex = source.getIncomingHandicapIndex();
        this.transport = source.getTransport();
        this.fairways = source.getFairways();
        this.fairwaysInRegulation = source.getFairwaysInRegulation();
        this.greensInRegulation = source.getGreensInRegulation();
        this.putts = source.getPutts();
        this.strokesGained = source.getStrokesGained();
        this.display = String.join(", ", source.getGolfer().getName(), source.getDate().toString(), source.getCourse().getName());
    }
}
