package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import java.util.function.Function;
import java.util.function.LongPredicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("stats")
@Log4j2
public class StatsApi {

  @RequestMapping(value = "rounds", produces = "text/csv")
  public String getRounds(@RequestParam(defaultValue = "csv") String fileType) {
    return generateDelimitedResponse(Optional.of(RoundTableRow.headers()), getRowSummaries(), Utils.lookupDelimOperator(fileType));
  }

  @RequestMapping(value = "shots", produces = "text/csv")
  public String getLatestShots(@RequestParam(defaultValue = "csv") String fileType) {
    return generateDelimitedResponse(Optional.of(ShotAnalysis.headers()), getLatestShots(), Utils.lookupDelimOperator(fileType));
  }

  @RequestMapping(value = "putting", produces = "text/csv")
  public String getPutting(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "true") String binned) {
    return generateDelimitedResponse(
        Optional.of(PuttingDistanceStat.headers()),
        getPuttingStats(Boolean.parseBoolean(binned)),
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "latest", produces = "text/csv")
  public String latestRound(@RequestParam(defaultValue = "csv") String fileType) {
    List<RoundDetailTableRow> columns = getTomStats()
        .compileTo18HoleRounds()
        .sortNewestToOldest()
        .findFirst()
        .map(RoundDetailTableColumn::compile)
        .map(RoundDetailTableColumn::toRows)
        .orElseThrow();

    return generateDelimitedResponse(Optional.empty(), columns, Utils.lookupDelimOperator(fileType));
  }

  @RequestMapping(value = "approaches", produces = "text/csv")
  public String approaches(@RequestParam(defaultValue = "csv") String fileType) {
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

    return generateDelimitedResponse(
        Optional.of(List.of("Approach Category", "Strokes Gained")),
        SimpleStat.compile(roundAvgSgByCat),
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "approaches-trend", produces = "text/csv")
  public String approaches(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "10") int window) {
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

    return toDelimitedString(results, fileType);
  }

  @RequestMapping(value = "strokesgained", produces = "text/csv")
  public String strokesGained(@RequestParam(defaultValue = "csv") String fileType,
                              @RequestParam(defaultValue = "10") int window) {
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

    return toDelimitedString(results, fileType);
  }

  @RequestMapping(value = "drivingdistance", produces = "text/csv")
  public String drivingDistance(@RequestParam(defaultValue = "csv") String fileType,
                                @RequestParam(defaultValue = "10") int window) {
    return generateRollingStat(getTomStats(), "Driving Distance",
        GolfRound::getP75DrivingDistance, window, fileType, Optional.of(val -> val > 0)
    );
  }

  @RequestMapping(value = "birdierate", produces = "text/csv")
  public String birdieRate(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "10") int window) {
    return generateRollingStat(getTomStats(), "Birdie Rate",
        round -> round.getHoles().getBirdieVsDoubleRatio(),
        window, fileType, Optional.empty()
    );
  }

  @RequestMapping(value = "greatrate", produces = "text/csv")
  public String greatRate(@RequestParam(defaultValue = "csv") String fileType,
                          @RequestParam(defaultValue = "10") int window) {
    return generateRollingStat(
        new GolfRoundStream(getTomStats().filter(round -> !round.getStrokesGainedByCategory().isEmpty()).toList()),
        "Great vs Bad Shots",
        round -> round.getShots().getGreatVsBadShots(),
        window, fileType, Optional.empty()
    );
  }

  private String generateRollingStat(GolfRoundStream rounds, String statName, ToLongFunction<GolfRound> statFunction,
                                     int window, String fileType, Optional<LongPredicate> filter) {
    var movingAverage = new MovingAverage(window);
    List<RollingStat> results = new ArrayList<>();
    rounds.forEachOrdered(round -> {
      long val = statFunction.applyAsLong(round);
      if (filter.isEmpty() || filter.get().test(val)) {
        results.add(new RollingStat(statName, movingAverage.next(new BigDecimal(val)), round));
      }
    });
    return toDelimitedString(results, fileType);
  }

  private String toDelimitedString(List<RollingStat> stats, String fileType) {
    var list = new ArrayList<>(stats);
    list.sort(Comparator.comparing(RollingStat::getName).thenComparing(RollingStat::getSequence));
    return generateDelimitedResponse(Optional.of(RollingStat.headers()), list, Utils.lookupDelimOperator(fileType));
  }

  private static List<RoundTableRow> getRowSummaries() {
    var rounds = getTomStats()
        .compileTo18HoleRounds()
        .sortNewestToOldest().toList();

    HandicapIndex index = HandicapIndex.newIndex(rounds);
    return rounds.stream().map(round -> new RoundTableRow(round, index)).toList();
  }

  private List<ShotAnalysis> getLatestShots() {
    GolfRound latestRound = getTomStats().compileTo18HoleRounds().sortNewestToOldest().findFirst().orElseThrow();
    return latestRound.getHoles().flatMap(
        hole -> hole.getShots().map(shot -> new ShotAnalysis(hole.getNumber(), shot))
    ).toList();
  }

  private List<PuttingDistanceStat> getPuttingStats(boolean binned) {
    var groupingFunction = binned ? PuttingDistanceStat.binDistance() : PuttingDistanceStat.distance();

    return getTomStats().flatMap(round -> round.getShots().greenShots())
        .collect(Collectors.groupingBy(groupingFunction, Collectors.toList()))
        .entrySet().stream().map(e -> new PuttingDistanceStat(e.getValue(), e.getKey()))
        .sorted(Comparator.comparing(PuttingDistanceStat::getDistance)).toList();
  }

  private String generateDelimitedResponse(Optional<List<String>> headers,
                                           Collection<? extends StatsApiValueSupplier> supplier,
                                           Function<Collection<String>, String> delimOp) {
    StringBuilder sb = new StringBuilder();
    headers.ifPresent(strings -> sb.append(delimOp.apply(strings)).append("\n"));
    supplier.forEach(row -> sb.append(delimOp.apply(row.values())).append("\n"));
    return sb.toString();
  }

  private static GolfRoundStream getTomStats() {
    return new GolfRoundStream(
        GolfRoundResourceManager.getInstance().getRoundsByGolfer().get("Tom")
    ).compileTo18HoleRounds();
  }


}
