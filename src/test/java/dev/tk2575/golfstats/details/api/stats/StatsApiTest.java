package dev.tk2575.golfstats.details.api.stats;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.core.stats.RoundSummaryStat;
import dev.tk2575.golfstats.core.stats.StatsService;
import dev.tk2575.golfstats.details.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"redis.host=localhost", "redis.port=6379"})
class StatsApiTest {
  
  @Spy
  private StatsService stats;
  
  @Mock
  private RedisService redis;
  
  @InjectMocks
  private StatsApi api;
  
  @BeforeEach
  void setup() {
    LocalDate date = LocalDate.of(2023, 1, 1);
    var meta = new RoundMeta(
        Golfer.newGolfer("Tom"),
        LocalDateTime.of(date, LocalTime.of(8, 0)),
        LocalDateTime.of(date, LocalTime.of(12, 0)),
        Course.of("Course"), 
        Tee.of("Tee", new BigDecimal("72"), new BigDecimal("113"), 72, 18)
    ); 
    var round = GolfRound.of(meta, 85, 14, 14, 18, 36, false);
    
    when(redis.getAllRounds(true)).thenReturn(List.of(round));
  }
  
  @Test
  void testRoundSummaries() throws HttpMediaTypeNotAcceptableException {
    when(stats.getRoundSummaries(anyList())).thenCallRealMethod();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Accept")).thenReturn("application/json");

    ResponseEntity<String> response = api.getRoundSummaries("Tom", request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<RoundSummaryStat> summaries = new Gson().fromJson(response.getBody(), new TypeToken<List<RoundSummaryStat>>(){}.getType());;
    assertNotNull(summaries);
    assertEquals(1, summaries.size());
    RoundSummaryStat summary = summaries.getFirst();
    assertEquals("2023-01-01", summary.getDate());
    assertEquals("Course", summary.getLabel());
  }
  
  void testLatestShots() throws HttpMediaTypeNotAcceptableException {
    when(stats.analyzeShots(any())).thenCallRealMethod();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Accept")).thenReturn("application/json");

    ResponseEntity<String> response = api.getLatestShots("Tom", request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<ShotAnalysis> shots = new Gson().fromJson(response.getBody(), new TypeToken<List<ShotAnalysis>>(){}.getType());;
    assertNotNull(shots);
    assertTrue(shots.isEmpty());
  }

}