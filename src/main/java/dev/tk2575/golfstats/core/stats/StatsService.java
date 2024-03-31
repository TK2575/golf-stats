package dev.tk2575.golfstats.core.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.api.stats.RoundDetailTableColumn;
import dev.tk2575.golfstats.details.api.stats.ShotAnalysis;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class StatsService {
  /**
   * Summarizes each round in the list into RoundTableRow
   *
   * @param rounds 18 hole rounds, sorted oldest to newest
   * @return List of RoundTableRow
   */
  public List<RoundTableRow> getRoundSummaries(List<GolfRound> rounds) {
    HandicapIndex index = HandicapIndex.newIndex(rounds);
    return rounds.stream().map(round -> new RoundTableRow(round, index)).toList();
  }

  /**
   * Analyzes each shot in the round and summarizes into ShotAnalysis
   * Previously used on the latest 18 hole round (.compileTo18HoleRounds().sortNewestToOldest().findFirst().orElseThrow();)
   *
   * @param round 18 hole round with shots
   * @return List of ShotAnalysis
   */
  public List<ShotAnalysis> analyzeShots(GolfRound round) {
    return round.getHoles().flatMap(
        hole -> hole.getShots().map(shot -> new ShotAnalysis(hole.getNumber(), shot))
    ).toList();
  }

  public List<PuttingDistanceStat> getPuttingStats(List<GolfRound> rounds) {
    return getPuttingStats(rounds, true);
  }

  public List<PuttingDistanceStat> getPuttingStats(List<GolfRound> rounds, boolean binned) {
    var groupingFunction = binned ? PuttingDistanceStat.binDistance() : PuttingDistanceStat.distance();

    return new GolfRoundStream(rounds).flatMap(round -> round.getShots().greenShots())
        .collect(Collectors.groupingBy(groupingFunction, Collectors.toList()))
        .entrySet().stream().map(e -> new PuttingDistanceStat(e.getValue(), e.getKey()))
        .sorted(Comparator.comparing(PuttingDistanceStat::getDistance)).toList();
  }

  /**
   * Details of each hole in the round, summarized into RoundDetailTableRow
   *
   * @param round 18 hole round, previously expecting the most recent
   * @return List of RoundDetailTableRow
   */
  public List<RoundDetailTableRow> getRoundDetail(GolfRound round) {
    return RoundDetailTableColumn.toRows(RoundDetailTableColumn.compile(round));
  }

  /**
   * @param rounds rounds with shots
   * @return mean strokes gained per round by approach category
   */
  public List<SimpleStat> getApproaches(List<GolfRound> rounds) {
    Map<ApproachBin, List<ApproachShot>> shotsByApproachBin = rounds.stream()
        .map(round -> ApproachShot.compile(round.getShots()))
        .flatMap(List::stream)
        .collect(Collectors.groupingBy(ApproachShot::getBin, Collectors.toList()));

    return shotsByApproachBin.entrySet().stream()
        .filter(e -> e.getKey() != null && !e.getKey().equals(ApproachBin.OTHER))
        .map(e -> ApproachShot.merge(e.getValue()))
        .map(bin -> new SimpleStat(bin.getBin().getLabel(), bin.getMeanStrokesGainedPerRound()))
        .toList();
  }

  public List<GolfRoundRollingStat> getApproachesRolling(List<GolfRound> rounds) {
    return getApproachesRolling(rounds, 10);
  }

  /**
   * Rolling average of strokes gained by approach category
   *
   * @param rounds sorted oldest to newest
   * @param window number of rounds to average
   * @return List of GolfRoundRollingStat
   */
  public List<GolfRoundRollingStat> getApproachesRolling(List<GolfRound> rounds, int window) {
    //TODO needs a solution for sample gaps
    List<GolfRoundRollingStat> results = new ArrayList<>();
    Map<String, MovingAverage> movingAverages = new HashMap<>();

    new GolfRoundStream(rounds).forEachOrdered(round ->
        round.getShots().collect(Collectors.groupingBy(ApproachBin.shotBinFunction, Collectors.toList()))
            .entrySet().stream()
            .filter(e -> e.getKey() != null && !e.getKey().equals(ApproachBin.OTHER))
            .forEach(e -> {
              var mAvg = movingAverages.getOrDefault(e.getKey().getLabel(), new MovingAverage(window));
              results.add(new GolfRoundRollingStat(
                  e.getKey().getLabel(), mAvg.next(new ShotStream(e.getValue()).totalStrokesGained()), round)
              );
              movingAverages.put(e.getKey().getLabel(), mAvg);
            }));

    return results;
  }

  public List<GolfRoundRollingStat> getStrokesGained(List<GolfRound> rounds) {
    return getStrokesGained(rounds, 10);
  }

  public List<GolfRoundRollingStat> getStrokesGained(List<GolfRound> rounds, int window) {
    List<GolfRoundRollingStat> results = new ArrayList<>();
    Map<String, MovingAverage> movingAverages = new HashMap<>();

    new GolfRoundStream(rounds).forEachOrdered(round -> {
      for (Map.Entry<String, BigDecimal> entry : round.getStrokesGainedByCategory().entrySet()) {
        var mAvg = movingAverages.getOrDefault(entry.getKey(), new MovingAverage(window));
        results.add(new GolfRoundRollingStat(
            "Strokes Gained: " + entry.getKey(),
            mAvg.next(entry.getValue()),
            round));
        movingAverages.put(entry.getKey(), mAvg);
      }
    });

    return results;
  }

  public List<GolfRoundRollingStat> getDrivingDistance(List<GolfRound> rounds) {
    return getDrivingDistance(rounds, 10);
  }

  public List<GolfRoundRollingStat> getDrivingDistance(List<GolfRound> rounds, int window) {
    return GolfRoundRollingStat.generate(new GolfRoundStream(rounds), "Driving Distance",
        GolfRound::getP75DrivingDistance, window, Optional.of(val -> val > 0)
    );
  }

  public List<GolfRoundRollingStat> birdieRate(List<GolfRound> rounds) {
    return birdieRate(rounds, 10);
  }

  public List<GolfRoundRollingStat> birdieRate(List<GolfRound> rounds, int window) {
    return GolfRoundRollingStat.generate(new GolfRoundStream(rounds), "Birdie Rate",
        round -> round.getHoles().getBirdieVsDoubleRatio(),
        window, Optional.empty()
    );
  }

  public List<GolfRoundRollingStat> greatRate(List<GolfRound> rounds) {
    return greatRate(rounds, 10);
  }

  /**
   * Rolling average of great vs bad shots
   *
   * @param rounds sorted oldest to newest, filtered to where round.getStrokesGainedByCategory() is not empty
   * @param window number of rounds to average
   * @return List of GolfRoundRollingStat
   */
  public List<GolfRoundRollingStat> greatRate(List<GolfRound> rounds, int window) {
    return GolfRoundRollingStat.generate(
        new GolfRoundStream(rounds),
        "Great vs Bad Shots",
        round -> round.getShots().getGreatVsBadShots(),
        window, Optional.empty()
    );
  }
}
