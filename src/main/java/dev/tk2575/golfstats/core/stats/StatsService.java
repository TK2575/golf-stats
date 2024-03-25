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
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class StatsService {
  public List<RoundTableRow> getRoundSummaries(List<GolfRound> rounds) {
    //TODO expect 18 hole rounds, sorted newest to oldest
    HandicapIndex index = HandicapIndex.newIndex(rounds);
    return rounds.stream().map(round -> new RoundTableRow(round, index)).toList();
  }
  
  public List<ShotAnalysis> getLatestShots(GolfRound round) {
    //TODO expect .compileTo18HoleRounds().sortNewestToOldest().findFirst().orElseThrow();
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

  public List<RoundDetailTableRow> getRoundDetail(GolfRound round) {
    //TODO expecting latest 18 hole round
    return RoundDetailTableColumn.toRows(RoundDetailTableColumn.compile(round));
  }
  
  public List<SimpleStat> getApproaches(List<GolfRound> rounds) {
    Map<ApproachCategory.Bin, List<BigDecimal>> roundSgByCat = new HashMap<>();

    new GolfRoundStream(rounds).map(round ->
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
  
  public List<GolfRoundRollingStat> getApproachesRolling(List<GolfRound> rounds) {
    return getApproachesRolling(rounds, 10);
  }
  
  public List<GolfRoundRollingStat> getApproachesRolling(List<GolfRound> rounds, int window) {
    //TODO needs a solution for sample gaps
    //TODO assumes ordered by date
    List<GolfRoundRollingStat> results = new ArrayList<>();
    Map<String, MovingAverage> movingAverages = new HashMap<>();

    new GolfRoundStream(rounds).forEachOrdered(round ->
        round.getShots().collect(Collectors.groupingBy(ApproachCategory.shotBinFunction, Collectors.toList()))
            .entrySet().stream()
            .filter(e -> e.getKey() != null && !e.getKey().equals(ApproachCategory.Bin.OTHER))
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
  
  public List<GolfRoundRollingStat> greatRate(List<GolfRound> rounds, int window) {
    //TODO expects .filter(round -> !round.getStrokesGainedByCategory().isEmpty())
    return GolfRoundRollingStat.generate(
        new GolfRoundStream(rounds),
        "Great vs Bad Shots",
        round -> round.getShots().getGreatVsBadShots(),
        window, Optional.empty()
    );
  }
}
