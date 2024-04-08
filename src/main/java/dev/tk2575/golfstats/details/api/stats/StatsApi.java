package dev.tk2575.golfstats.details.api.stats;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.gson.Gson;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.stats.*;
import dev.tk2575.golfstats.details.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
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

  @GetMapping(value = "rounds", produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv", "text/tab-separated-values"})
  public ResponseEntity<String> getRoundSummaries(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    return mediaTypeConversion(request, stats.getRoundSummaries(rounds));
  }

  @RequestMapping(value = "latest-shots", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getLatestShots(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var shotAnalyses = new GolfRoundStream(getRounds(golfer))
        .compileTo18HoleRounds()
        .newestRound().map(stats::analyzeShots)
        .orElseGet(List::of);
    
    return mediaTypeConversion(request, shotAnalyses);
  }

  @RequestMapping(value = "putting", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getPutting(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().toList();
    return mediaTypeConversion(request, stats.getPuttingStats(rounds));
  }

  @RequestMapping(value = "latest-round", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getLatestRound(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var details = new GolfRoundStream(getRounds(golfer))
            .compileTo18HoleRounds()
            .newestRound()
            .map(stats::getRoundDetail)
            .orElseGet(List::of);
    
    return mediaTypeConversion(request, details);
  }

  @RequestMapping(value = "approaches", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getApproaches(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().toList();
    return mediaTypeConversion(request, stats.getApproaches(rounds));
  }
  
  @RequestMapping(value = "trend-approaches", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getApproachesTrend(
      @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
      @RequestParam(defaultValue = "10") int window,
          HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().toList();
    List<ApproachPoint> approachesRolling = stats.getApproachesRolling(rounds, window);
    return mediaTypeConversion(request, approachesRolling);
  }

  @RequestMapping(value = "trend-strokes-gained", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getStrokesGainedTrend(
          @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
          @RequestParam(defaultValue = "10") int window,
          HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    List<GolfRoundRollingStat> strokesGained = stats.getStrokesGained(rounds, window);
    return mediaTypeConversion(request, strokesGained);
  }

  @RequestMapping(value = "trend-driving-distance", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getDrivingDistanceTrend(
          @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
          @RequestParam(defaultValue = "10") int window, 
          HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    List<GolfRoundRollingStat> drivingDistance = stats.getDrivingDistance(rounds, window);
    return mediaTypeConversion(request, drivingDistance);
  }

  @RequestMapping(value = "trend-birdie-rate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getBirdieRateTrend(
          @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
          @RequestParam(defaultValue = "10") int window, 
          HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    List<GolfRoundRollingStat> birdieRate = stats.getBirdieRate(rounds, window);
    return mediaTypeConversion(request, birdieRate);
  }

  @RequestMapping(value = "trend-great-rate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getRateRateTrend(
        @NonNull @NotEmpty @RequestParam(value = "golfer", defaultValue = "Tom") String golfer,
        @RequestParam(defaultValue = "10") int window, 
          HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
    var rounds = new GolfRoundStream(getRounds(golfer)).compileTo18HoleRounds().sortOldestToNewest().toList();
    List<GolfRoundRollingStat> greatRate = stats.getGreatRate(rounds, window);
    return mediaTypeConversion(request, greatRate);
  }
  
  private List<GolfRound> getRounds(String golferName) {
    return new GolfRoundStream(redis.getAllRounds(true))
        .filter(round -> round.getGolfer().getName().equalsIgnoreCase(golferName))
        .toList();
  }
  
  private static ResponseEntity<String> mediaTypeConversion(HttpServletRequest request, List<?> values) throws HttpMediaTypeNotAcceptableException {
    String acceptHeader = request.getHeader("Accept");
    if (acceptHeader == null || acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
      try {
        String json = new Gson().toJson(values);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(json);
      }
      catch (Exception e) {
        log.error("Failed to convert to JSON", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to convert to JSON");
      }
    }

    if (List.of("text/csv", "text/tab-separated-values").contains(acceptHeader)) {
      try {
        String result = "";
        if (!values.isEmpty()) {
          Class<?> clazz = values.stream().findAny().get().getClass();
          var mapper = new CsvMapper();
          var schema = mapper
              .schemaFor(clazz)
              .withHeader()
              .withColumnSeparator(acceptHeader.contains("csv") ? ',' : '\t');
          result = mapper.writer(schema).writeValueAsString(values);
        }
        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(acceptHeader))
            .body(result);
      } catch (Exception e) {
        log.error("Failed to convert to CSV", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to convert to CSV");
      }
    }

    throw new HttpMediaTypeNotAcceptableException("Requested output type is not supported");
  }
  
}
