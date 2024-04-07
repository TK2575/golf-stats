package dev.tk2575.golfstats.core.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.api.stats.RoundDetailTableColumn;
import dev.tk2575.golfstats.details.api.stats.ShotAnalysis;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
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
    Map<ApproachBin, List<ApproachSummary>> shotsByApproachBin = rounds.stream()
        .map(round -> ApproachSummary.compile(round.getShots()))
        .flatMap(List::stream)
        .collect(Collectors.groupingBy(ApproachSummary::getBin, Collectors.toList()));

    return shotsByApproachBin.entrySet().stream()
        .filter(e -> e.getKey() != null && !e.getKey().equals(ApproachBin.OTHER))
        .map(e -> ApproachSummary.merge(e.getValue()))
        .map(summary -> new SimpleStat(summary.getBin().getLabel(), summary.getMeanStrokesGainedPerRound()))
        .toList();
  }

  public List<ApproachPoint> getApproachesRolling(List<GolfRound> rounds) {
    return getApproachesRolling(rounds, 10);
  }

  /**
   * Rolling average of strokes gained by approach category
   *
   * @param rounds sorted oldest to newest
   * @param window number of rounds to average
   * @return List of ApproachPoint for building table of avg strokes gained (date x bin)
   */
  public List<ApproachPoint> getApproachesRolling(List<GolfRound> rounds, int window) {
    List<ApproachPoint> results = new ArrayList<>();
    Map<ApproachBin, ApproachPoint> cursors = new EnumMap<>(ApproachBin.class);
    
    new GolfRoundStream(rounds).forEachOrdered(round -> {
      Map<ApproachBin, List<Shot>> shotsByBin = round.getShots().collect(Collectors.groupingBy(ApproachBin.shotBinFunction, Collectors.toList()));
      Arrays.stream(ApproachBin.values()).forEach(bin -> {
        if (bin != null && !bin.equals(ApproachBin.OTHER)) {
          var cursor = cursors.getOrDefault(bin, new ApproachPoint(round.getDate(), bin, BigDecimal.ZERO, new MovingAverage(window)));
          BigDecimal sg = new ShotStream(shotsByBin.getOrDefault(bin, List.of())).totalStrokesGained();
          BigDecimal nextAvg = cursor.getMovingAverage().next(sg).getKey();
          var point = new ApproachPoint(round.getDate(), bin, nextAvg, cursor.getMovingAverage());
          results.add(point);
          cursors.put(bin, point);
        }
      });
    });

    return results;
  }

  /**
   *
   * @param rounds golf rounds with shots
   * @param window moving average window size
   * @return moving average strokes gained per shot category
   */
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

  /**
   *
   * @param rounds golf rounds with shots, sorted oldest to newest
   * @return
   */
  public List<GolfRoundRollingStat> getDrivingDistance(List<GolfRound> rounds) {
    return getDrivingDistance(rounds, 10);
  }

  public List<GolfRoundRollingStat> getDrivingDistance(List<GolfRound> rounds, int window) {
    return GolfRoundRollingStat.generate(new GolfRoundStream(rounds), "Driving Distance",
        GolfRound::getP75DrivingDistance, window, Optional.of(val -> val > 0)
    );
  }

  public List<GolfRoundRollingStat> getBirdieRate(List<GolfRound> rounds, int window) {
    return GolfRoundRollingStat.generate(new GolfRoundStream(rounds), "Birdie Rate",
        round -> round.getHoles().getBirdieVsDoubleRatio(),
        window, Optional.empty()
    );
  }

  /**
   * Rolling average of great vs bad shots
   *
   * @param rounds sorted oldest to newest, filtered to where round.getStrokesGainedByCategory() is not empty
   * @param window number of rounds to average
   * @return List of GolfRoundRollingStat
   */
  public List<GolfRoundRollingStat> getGreatRate(List<GolfRound> rounds, int window) {
    return GolfRoundRollingStat.generate(
        new GolfRoundStream(rounds),
        "Great vs Bad Shots",
        round -> round.getShots().getGreatVsBadShots(),
        window, Optional.empty()
    );
  }
}
