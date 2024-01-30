package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ApproachShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.AroundGreenShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.GreenShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.RecoveryShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.TeeShotCategory;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
public class RoundTableRow implements StatsApiValueSupplier {
  private final LocalDate date;
  private final String label;
  private final Integer strokes;
  private final Integer score;
  private final BigDecimal sgTotal;
  private final Optional<BigDecimal> sgTee;
  private final Optional<BigDecimal> sgApproach;
  private final Optional<BigDecimal> sgShortGame;
  private final Optional<BigDecimal> sgPutting;
  private final Optional<BigDecimal> sgRecovery;
  private final Long p75DrivingDistance;
  private final Long longestDrive;
  private final Long greatShots;
  private final Long poorShots;
  private final BigDecimal differential;
  private final Optional<BigDecimal> resultantHandicapIndex;
  private final Long yards;
  
  public RoundTableRow(@NonNull GolfRound round, HandicapIndex index) {
    this.date = round.getDate();
    this.label = round.getCourse().getName();
    this.strokes = round.getStrokes();
    this.score = round.getScore();
    this.sgTotal = round.getStrokesGained();
    
    Map<String, BigDecimal> sgMap = round.getHoles().strokesGainedByShotType();
    this.sgTee = Optional.ofNullable(sgMap.get(ShotCategory.tee().getLabel()));
    this.sgApproach = Optional.ofNullable(sgMap.get(ShotCategory.approach().getLabel()));
    this.sgShortGame = Optional.ofNullable(sgMap.get(ShotCategory.aroundGreen().getLabel()));
    this.sgPutting = Optional.ofNullable(sgMap.get(ShotCategory.green().getLabel()));
    this.sgRecovery = Optional.ofNullable(sgMap.get(ShotCategory.recovery().getLabel()));
    this.p75DrivingDistance = round.getP75DrivingDistance();
    this.longestDrive = round.getLongestDrive();
    this.greatShots = round.getShots().countGreatShots();
    this.poorShots = round.getShots().countBadShots();
    this.differential = round.getScoreDifferential();
    this.resultantHandicapIndex = Optional.ofNullable(index.getRevisionHistory()
        .getOrDefault(round.getDate(), round.getIncomingHandicapIndex()));
    this.yards = round.getYards();
  }

  public static List<String> headers() {
    return List.of(
        "Date",
        "Course",
        "Strokes",
        "Score",
        "TOT",
        "OTT",
        "APP",
        "ARG",
        "PUTT",
        "RCVR",
        "p75 Drive",
        "Great Shots",
        "Poor Shots",
        "Differential",
        "Handicap Index",
        "Yards");
  }

  public List<String> values() {
    return List.of(
        date.toString(),
        label,
        strokes.toString(),
        score.toString(),
        sgTotal.toString(),
        sgTee.map(BigDecimal::toPlainString).orElse(""),
        sgApproach.map(BigDecimal::toPlainString).orElse(""),
        sgShortGame.map(BigDecimal::toPlainString).orElse(""),
        sgPutting.map(BigDecimal::toPlainString).orElse(""),
        sgRecovery.map(BigDecimal::toPlainString).orElse(""),
        p75DrivingDistance.toString(),
        greatShots.toString(),
        poorShots.toString(),
        differential.toPlainString(),
        resultantHandicapIndex.map(BigDecimal::toPlainString).orElse(""),
        yards.toString()
      );
  }
}
