package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.stats.PuttingDistanceStat;
import dev.tk2575.golfstats.core.stats.RollingStat;
import dev.tk2575.golfstats.core.stats.RoundTableRow;
import dev.tk2575.golfstats.core.stats.StatsApiValueSupplier;
import dev.tk2575.golfstats.core.stats.StatsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("stats")
@Log4j2
public class StatsApi {
  
  private final StatsService svc;
  
  @Autowired
  public StatsApi(StatsService svc) {
    this.svc = svc;
  }

  @RequestMapping(value = "rounds", produces = "text/csv")
  public String getRounds(@RequestParam(defaultValue = "csv") String fileType) {
    return generateDelimitedResponse(
        Optional.of(RoundTableRow.headers()), 
        svc.getRoundSummaries(), 
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "shots", produces = "text/csv")
  public String getLatestShots(@RequestParam(defaultValue = "csv") String fileType) {
    return generateDelimitedResponse(
        Optional.of(ShotAnalysis.headers()), 
        svc.getLatestShots(), 
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "putting", produces = "text/csv")
  public String getPutting(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "true") String binned) {
    return generateDelimitedResponse(
        Optional.of(PuttingDistanceStat.headers()),
        svc.getPuttingStats(Boolean.parseBoolean(binned)),
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "latest", produces = "text/csv")
  public String latestRound(@RequestParam(defaultValue = "csv") String fileType) {
    return generateDelimitedResponse(
        Optional.empty(), 
        svc.getLatestRound(), 
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "approaches", produces = "text/csv")
  public String approaches(@RequestParam(defaultValue = "csv") String fileType) {
    return generateDelimitedResponse(
        Optional.of(List.of("Approach Category", "Strokes Gained")),
        svc.getApproaches(),
        Utils.lookupDelimOperator(fileType)
    );
  }

  @RequestMapping(value = "approaches-trend", produces = "text/csv")
  public String approachesTrend(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "10") int window) {
    return toDelimitedString(svc.getApproachesRolling(window), fileType);
  }

  @RequestMapping(value = "strokesgained", produces = "text/csv")
  public String strokesGained(@RequestParam(defaultValue = "csv") String fileType,
                              @RequestParam(defaultValue = "10") int window) {
    return toDelimitedString(svc.getStrokesGained(window), fileType);
  }

  @RequestMapping(value = "drivingdistance", produces = "text/csv")
  public String drivingDistance(@RequestParam(defaultValue = "csv") String fileType,
                                @RequestParam(defaultValue = "10") int window) {
    return toDelimitedString(svc.getDrivingDistance(window), fileType);
  }

  @RequestMapping(value = "birdierate", produces = "text/csv")
  public String birdieRate(@RequestParam(defaultValue = "csv") String fileType,
                           @RequestParam(defaultValue = "10") int window) {
    return toDelimitedString(svc.birdieRate(window), fileType);
  }

  @RequestMapping(value = "greatrate", produces = "text/csv")
  public String greatRate(@RequestParam(defaultValue = "csv") String fileType,
                          @RequestParam(defaultValue = "10") int window) {
    return toDelimitedString(svc.greatRate(window), fileType);
  }

  private String toDelimitedString(List<RollingStat> stats, String fileType) {
    var list = new ArrayList<>(stats);
    list.sort(Comparator.comparing(RollingStat::getName).thenComparing(RollingStat::getSequence));
    return generateDelimitedResponse(Optional.of(RollingStat.headers()), list, Utils.lookupDelimOperator(fileType));
  }

  private String generateDelimitedResponse(Optional<List<String>> headers,
                                           Collection<? extends StatsApiValueSupplier> supplier,
                                           Function<Collection<String>, String> delimOp) {
    StringBuilder sb = new StringBuilder();
    headers.ifPresent(strings -> sb.append(delimOp.apply(strings)).append("\n"));
    supplier.forEach(row -> sb.append(delimOp.apply(row.values())).append("\n"));
    return sb.toString();
  }
  
}
