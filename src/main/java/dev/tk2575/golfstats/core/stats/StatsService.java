package dev.tk2575.golfstats.core.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.imports.GolfRoundImporter;
import dev.tk2575.golfstats.details.api.stats.RoundDetailTableColumn;
import dev.tk2575.golfstats.details.api.stats.ShotAnalysis;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.LongPredicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class StatsService {
  public List<RoundTableRow> getRoundSummaries() {
    var rounds = getTomStats()
        .compileTo18HoleRounds()
        .sortNewestToOldest().toList();

    HandicapIndex index = HandicapIndex.newIndex(rounds);
    return rounds.stream().map(round -> new RoundTableRow(round, index)).toList();
  }
  
  public List<ShotAnalysis> getLatestShots() {
    GolfRound latestRound = getTomStats().compileTo18HoleRounds().sortNewestToOldest().findFirst().orElseThrow();
    return latestRound.getHoles().flatMap(
        hole -> hole.getShots().map(shot -> new ShotAnalysis(hole.getNumber(), shot))
    ).toList();
  }
  
  public List<PuttingDistanceStat> getPuttingStats() {
    return getPuttingStats(true);
  }
  
  public List<PuttingDistanceStat> getPuttingStats(boolean binned) {
    var groupingFunction = binned ? PuttingDistanceStat.binDistance() : PuttingDistanceStat.distance();

    return getTomStats().flatMap(round -> round.getShots().greenShots())
        .collect(Collectors.groupingBy(groupingFunction, Collectors.toList()))
        .entrySet().stream().map(e -> new PuttingDistanceStat(e.getValue(), e.getKey()))
        .sorted(Comparator.comparing(PuttingDistanceStat::getDistance)).toList();
  }

  public List<RoundDetailTableRow> getLatestRound() {
    return getTomStats()
        .compileTo18HoleRounds()
        .sortNewestToOldest()
        .findFirst()
        .map(RoundDetailTableColumn::compile)
        .map(RoundDetailTableColumn::toRows)
        .orElseThrow();
  }
  
  public List<SimpleStat> getApproaches() {
    Map<ApproachCategory.Bin, List<BigDecimal>> roundSgByCat = new HashMap<>();

    getTomStats().map(round ->
        round.getShots()
            .collect(Collectors.groupingBy(ApproachCategory.shotBinFunction, Collectors.toList()))
            .entrySet().stream()
            .filter(e -> e.getKey() != null && !e.getKey().equals(ApproachCategory.Bin.OTHER))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> new ShotStream(e.getValue()).totalStrokesGained())
            )
    //TODO reduce instead of forEach?
    ).forEach(sgByCat -> 
        sgByCat.forEach((cat, sgList) -> 
            roundSgByCat.merge(cat, List.of(sgList), (l1, l2) -> 
                Stream.of(l1, l2).flatMap(Collection::stream).toList()
            )
        )
    );

    Map<ApproachCategory.Bin, BigDecimal> roundAvgSgByCat = new TreeMap<>();
    roundSgByCat.forEach((k, v) ->
        roundAvgSgByCat.put(k, v.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(v.size()), 2, RoundingMode.HALF_UP)));

    return SimpleStat.compile(roundAvgSgByCat);
  }
  
  public List<RollingStat> getApproachesRolling() {
    return getApproachesRolling(10);
  }
  
  public List<RollingStat> getApproachesRolling(int window) {
    //TODO needs a solution for sample gaps
    List<RollingStat> results = new ArrayList<>();
    Map<String, MovingAverage> movingAverages = new HashMap<>();

    getTomStats().forEachOrdered(round ->
        round.getShots().collect(Collectors.groupingBy(ApproachCategory.shotBinFunction, Collectors.toList()))
            .entrySet().stream()
            .filter(e -> e.getKey() != null && !e.getKey().equals(ApproachCategory.Bin.OTHER))
            .forEach(e -> {
              var mAvg = movingAverages.getOrDefault(e.getKey().getLabel(), new MovingAverage(window));
              results.add(new RollingStat(
                  e.getKey().getLabel(), mAvg.next(new ShotStream(e.getValue()).totalStrokesGained()), round)
              );
              movingAverages.put(e.getKey().getLabel(), mAvg);
            }));

    return results;
  }
  
  public List<RollingStat> getStrokesGained() {
    return getStrokesGained(10);
  }
  
  public List<RollingStat> getStrokesGained(int window) {
    List<RollingStat> results = new ArrayList<>();
    Map<String, MovingAverage> movingAverages = new HashMap<>();

    getTomStats().forEachOrdered(round -> {
      for (Map.Entry<String, BigDecimal> entry : round.getStrokesGainedByCategory().entrySet()) {
        var mAvg = movingAverages.getOrDefault(entry.getKey(), new MovingAverage(window));
        results.add(new RollingStat(
            "Strokes Gained: " + entry.getKey(),
            mAvg.next(entry.getValue()),
            round));
        movingAverages.put(entry.getKey(), mAvg);
      }
    });

    return results;
  }
  
  public List<RollingStat> getDrivingDistance() {
    return getDrivingDistance(10);
  }
  
  public List<RollingStat> getDrivingDistance(int window) {
    return generateRollingStat(getTomStats(), "Driving Distance",
        GolfRound::getP75DrivingDistance, window, Optional.of(val -> val > 0)
    );
  }
  
  public List<RollingStat> birdieRate() {
    return birdieRate(10);
  }
  
  public List<RollingStat> birdieRate(int window) {
    return generateRollingStat(getTomStats(), "Birdie Rate",
        round -> round.getHoles().getBirdieVsDoubleRatio(),
        window, Optional.empty()
    );
  }
  
  public List<RollingStat> greatRate() {
    return greatRate(10);
  }
  
  public List<RollingStat> greatRate(int window) {
    return generateRollingStat(
        new GolfRoundStream(getTomStats().filter(round -> !round.getStrokesGainedByCategory().isEmpty()).toList()),
        "Great vs Bad Shots",
        round -> round.getShots().getGreatVsBadShots(),
        window, Optional.empty()
    );
  }
  
  //TODO move to RollingStat?
  private List<RollingStat> generateRollingStat(GolfRoundStream rounds, String statName, ToLongFunction<GolfRound> statFunction,
                                     int window, Optional<LongPredicate> filter) {
    var movingAverage = new MovingAverage(window);
    List<RollingStat> results = new ArrayList<>();
    rounds.forEachOrdered(round -> {
      long val = statFunction.applyAsLong(round);
      if (filter.isEmpty() || filter.get().test(val)) {
        results.add(new RollingStat(statName, movingAverage.next(new BigDecimal(val)), round));
      }
    });
    return results;
  }
  
  //TODO parameterize golfer? get from database eventually
  private static GolfRoundStream getTomStats() {
    return new GolfRoundStream(
        GolfRoundImporter.getInstance().getRoundsByGolfer().get("Tom")
    ).compileTo18HoleRounds();
  }
}
