package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.stats.*;
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

  private List<GolfRound> getRounds(String golferName) {
    return new GolfRoundStream(redis.getAllRounds(true))
        .filter(round -> round.getGolfer().getName().equalsIgnoreCase(golferName))
        .toList();
  }

  @RequestMapping(value = "rounds", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<RoundTableRow>> getRoundSummaries(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    return ResponseEntity.ok(stats.getRoundSummaries(rounds));  
  }
  
  @RequestMapping(value = "latest-shots", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ShotAnalysis>> getLatestShots(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer) {
    return new GolfRoundStream(getRounds(golfer))
            .compileTo18HoleRounds()
            .newestRound()
            .map(golfRound -> ResponseEntity.ok(stats.analyzeShots(golfRound)))
            .orElseGet(() -> ResponseEntity.ok(List.of()));
  }

  @RequestMapping(value = "putting", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<PuttingDistanceStat>> getPutting(@NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().toList();
    return ResponseEntity.ok(stats.getPuttingStats(rounds));
  }

  @RequestMapping(value = "latest-round", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<RoundDetailTableRow>> getLatestRound(@NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer) {
    //TODO maybe a different object
    return new GolfRoundStream(getRounds(golfer))
            .compileTo18HoleRounds()
            .newestRound()
            .map(round -> ResponseEntity.ok(stats.getRoundDetail(round)))
            .orElseGet(() -> ResponseEntity.ok(List.of()));
  }

  @RequestMapping(value = "approaches", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SimpleStat>> getApproaches(@NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().toList();
    return ResponseEntity.ok(stats.getApproaches(rounds));
  }
  
  @RequestMapping(value = "trend-approaches", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ApproachPoint>> getApproachesTrend(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      @RequestParam(defaultValue = "10") int window) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().toList();
    return ResponseEntity.ok(stats.getApproachesRolling(rounds, window));
  }

  @RequestMapping(value = "trend-strokes-gained", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GolfRoundRollingStat>> getStrokesGainedTrend(
          @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
          @RequestParam(defaultValue = "10") int window) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    return ResponseEntity.ok(stats.getStrokesGained(rounds, window));
  }

  @RequestMapping(value = "trend-driving-distance", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GolfRoundRollingStat>> getDrivingDistanceTrend(
          @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
          @RequestParam(defaultValue = "10") int window) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    return ResponseEntity.ok(stats.getDrivingDistance(rounds, window));
  }

  @RequestMapping(value = "trend-birdie-rate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GolfRoundRollingStat>> getBirdieRateTrend(
          @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
          @RequestParam(defaultValue = "10") int window) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    return ResponseEntity.ok(stats.getBirdieRate(rounds, window));
  }

  @RequestMapping(value = "trend-great-rate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GolfRoundRollingStat>> getRateRateTrend(
        @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
        @RequestParam(defaultValue = "10") int window) {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    return ResponseEntity.ok(stats.getGreatRate(rounds, window));
  }

  //TODO convert to use stats service (still want a csv output endpoint)
  /*
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
