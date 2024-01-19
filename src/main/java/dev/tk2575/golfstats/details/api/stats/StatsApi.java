package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                           @RequestParam(defaultValue = "false") String binned) {
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

  @RequestMapping(value = "drivingdistance", produces = "text/csv")
  public String drivingDistance(@RequestParam(defaultValue = "csv") String fileType,
                                @RequestParam(defaultValue = "10") int window) {
    var driveAvg = new MovingAverage(window);
    List<RollingStat> results = new ArrayList<>();
    getTomStats().forEachOrdered(round -> {
      var driveDist = round.getP75DrivingDistance();
      if (driveDist > 0) {
        results.add(new RollingStat(
            "p75 Driving Distance",
            driveAvg.next(new BigDecimal(driveDist)),
            round));
      }
    });
    return generateDelimitedResponse(Optional.of(RollingStat.headers()), results, Utils.lookupDelimOperator(fileType));
  }

  @RequestMapping(value = "birdierate", produces = "text/csv")
  public String birdieRate(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "10") int window) {
    var birdieAvg = new MovingAverage(window);
    List<RollingStat> results = new ArrayList<>();
    getTomStats().forEachOrdered(round -> {
      results.add(new RollingStat(
          "Birdie vs Double Ratio",
          birdieAvg.next(new BigDecimal(round.getBirdieVsDoubleRatio())),
          round)
      );
    });
    return generateDelimitedResponse(Optional.of(RollingStat.headers()), results, Utils.lookupDelimOperator(fileType));
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

    results.sort(Comparator.comparing(RollingStat::getName).thenComparing(RollingStat::getSequence));
    return generateDelimitedResponse(Optional.of(RollingStat.headers()), results, Utils.lookupDelimOperator(fileType));
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
