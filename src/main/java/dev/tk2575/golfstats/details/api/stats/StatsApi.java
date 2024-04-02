package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.stats.RoundTableRow;
import dev.tk2575.golfstats.core.stats.StatsService;
import dev.tk2575.golfstats.details.redis.RedisService;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Predicate;

@RestController
@RequestMapping("stats")
@Log4j2
public class StatsApi {
  
  private final RedisService redis;
  private final StatsService stats;
  
  @Autowired
  public StatsApi(RedisService redis, StatsService stats) {
    this.redis = redis;
    this.stats = stats;
  }
  
  @RequestMapping("count")
  public int getRoundCount() {
    return redis.countRounds();
  }

  private static Predicate<GolfRound> roundsForGolfer(String name) {
    return round -> round.getGolfer().getName().equalsIgnoreCase(name);
  }

  @RequestMapping(value = "roundSummaries", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<RoundTableRow>> getRoundSummaries(@NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer) {
    List<GolfRound> rounds = redis.getAllRounds(true).stream().filter(roundsForGolfer(golfer)).toList();
    return ResponseEntity.ok(stats.getRoundSummaries(rounds));  
  }

  //TODO convert to use stats service (still want a csv output endpoint)
  /*
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
        svc.getRoundDetail(), 
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

  private String toDelimitedString(List<GolfRoundRollingStat> stats, String fileType) {
    var list = new ArrayList<>(stats);
    list.sort(Comparator.comparing(GolfRoundRollingStat::getName).thenComparing(GolfRoundRollingStat::getSequence));
    return generateDelimitedResponse(Optional.of(GolfRoundRollingStat.headers()), list, Utils.lookupDelimOperator(fileType));
  }

  private String generateDelimitedResponse(Optional<List<String>> headers,
                                           Collection<? extends StatsApiValueSupplier> supplier,
                                           Function<Collection<String>, String> delimOp) {
    StringBuilder sb = new StringBuilder();
    headers.ifPresent(strings -> sb.append(delimOp.apply(strings)).append("\n"));
    supplier.forEach(row -> sb.append(delimOp.apply(row.values())).append("\n"));
    return sb.toString();
  }*/
  
}
