package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ApproachShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.AroundGreenShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.GreenShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.RecoveryShotCategory;
import dev.tk2575.golfstats.core.golfround.shotbyshot.TeeShotCategory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public class RoundTableRow {
  private final LocalDate date;
  private final String label;
  private final Integer score;
  private final Integer netScore;
  private final BigDecimal sgTotal;
  private final Optional<BigDecimal> sgTee;
  private final Optional<BigDecimal> sgApproach;
  private final Optional<BigDecimal> sgShortGame;
  private final Optional<BigDecimal> sgPutting;
  private final Optional<BigDecimal> sgRecovery;
  
  public RoundTableRow(@NonNull GolfRound round) {
    this.date = round.getDate();
    this.label = round.getCourse().getName();
    this.score = round.getStrokes();
    this.netScore = round.getNetScore();
    this.sgTotal = round.getStrokesGained();
    
    Map<String, BigDecimal> sgMap = round.getHoles().strokesGainedByShotType();
    this.sgTee = Optional.ofNullable(sgMap.get(new TeeShotCategory().getLabel()));
    this.sgApproach = Optional.ofNullable(sgMap.get(new ApproachShotCategory().getLabel()));
    this.sgShortGame = Optional.ofNullable(sgMap.get(new AroundGreenShotCategory().getLabel()));
    this.sgPutting = Optional.ofNullable(sgMap.get(new GreenShotCategory().getLabel()));
    this.sgRecovery = Optional.ofNullable(sgMap.get(new RecoveryShotCategory().getLabel()));
  }

  public static List<String> headers() {
    return List.of(
        "Date",
        "Course",
        "Score",
        "To Par",
        "TOT",
        "OTT",
        "APP",
        "ARG",
        "PUTT",
        "RCVR");
  }

  public List<String> values() {
    return List.of(
        date.toString(),
        label,
        score.toString(),
        netScore.toString(),
        sgTotal.toString(),
        sgTee.isEmpty() ? "" : sgTee.get().toString(),
        sgApproach.isEmpty() ? "" : sgApproach.get().toString(),
        sgShortGame.isEmpty() ? "" : sgShortGame.get().toString(),
        sgPutting.isEmpty() ? "" : sgPutting.get().toString(),
        sgRecovery.isEmpty() ? "" : sgRecovery.get().toString());
  }
}
