package dev.tk2575.golfstats.core.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotCategory;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class RoundSummaryStat {
  private static final String DATE_FORMAT = "yyyy-MM-dd"; 
  
  private final String date;
  private final String label;
  private final Integer strokes;
  private final Integer score;
  private final BigDecimal sgTotal;
  private final BigDecimal sgTee;
  private final BigDecimal sgApproach;
  private final BigDecimal sgShortGame;
  private final BigDecimal sgPutting;
  private final BigDecimal sgRecovery;
  private final Long p75DrivingDistance;
  private final Long longestDrive;
  private final Long greatShots;
  private final Long poorShots;
  private final BigDecimal differential;
  private final BigDecimal resultantHandicapIndex;
  private final Long yards;
  
  public RoundSummaryStat(@NonNull GolfRound round, HandicapIndex index) {
    this.date = round.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    this.label = round.getCourse().getName();
    this.strokes = round.getStrokes();
    this.score = round.getScore();
    this.sgTotal = round.getStrokesGained();
    
    Map<String, BigDecimal> sgMap = round.getHoles().strokesGainedByShotType();
    this.sgTee = sgMap.get(ShotCategory.tee().getLabel());
    this.sgApproach = sgMap.get(ShotCategory.approach().getLabel());
    this.sgShortGame = sgMap.get(ShotCategory.aroundGreen().getLabel());
    this.sgPutting = sgMap.get(ShotCategory.green().getLabel());
    this.sgRecovery = sgMap.get(ShotCategory.recovery().getLabel());
    this.p75DrivingDistance = round.getP75DrivingDistance();
    this.longestDrive = round.getLongestDrive();
    this.greatShots = round.getShots().countGreatShots();
    this.poorShots = round.getShots().countBadShots();
    this.differential = round.getScoreDifferential();
    this.resultantHandicapIndex = index.getRevisionHistory()
        .getOrDefault(round.getDate(), round.getIncomingHandicapIndex());
    this.yards = round.getYards();
  }
}
