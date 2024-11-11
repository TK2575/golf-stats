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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"redis.host=localhost", "redis.port=6379", "notion.token=secret_token"})
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
    RoundSummaryStat mockSummary = new RoundSummaryStat("2024-01-01", "Mock Label", 85, 14, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, 0L, 0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
    when(stats.getRoundSummaries(anyList())).thenReturn(List.of(mockSummary));
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Accept")).thenReturn("application/json");

    ResponseEntity<String> response = api.getRoundSummaries("Tom", request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    
    List<RoundSummaryStat> summaries = new Gson().fromJson(response.getBody(), new TypeToken<List<RoundSummaryStat>>(){}.getType());;
    assertNotNull(summaries);
    assertEquals(1, summaries.size());
    
    RoundSummaryStat summary = summaries.getFirst();
    assertEquals("2024-01-01", summary.getDate());
    assertEquals("Mock Label", summary.getLabel());
  }
  
  @Test
  void testLatestShots() throws HttpMediaTypeNotAcceptableException {
    ShotAnalysis mockAnalysis = new ShotAnalysis(1, 1, "Lie", "Category", 0, "unit", 0, "unit", BigDecimal.ZERO, "Result Lie", 0, "unit", "0", "Description", 0L);
    when(stats.analyzeShots(any())).thenReturn(List.of(mockAnalysis));
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Accept")).thenReturn("application/json");

    ResponseEntity<String> response = api.getLatestShots("Tom", request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    
    List<ShotAnalysis> shots = new Gson().fromJson(response.getBody(), new TypeToken<List<ShotAnalysis>>(){}.getType());;
    assertNotNull(shots);
    assertFalse(shots.isEmpty());
    
    ShotAnalysis shot = shots.getFirst();
    assertEquals(1, shot.getHole());
    assertEquals("Lie", shot.getLie());
    assertEquals("Category", shot.getCategory());
  }
  
  @Test
  void testApproachesTrend_requestJson() throws HttpMediaTypeNotAcceptableException {
    Map<String, BigDecimal> mapMock = Map.of(
        "Cat 1", new BigDecimal("1"), "Cat 2", new BigDecimal("2"), "Cat 3", new BigDecimal("3")
    );
    when(stats.getApproaches(anyList())).thenReturn(mapMock);
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Accept")).thenReturn("application/json");

    ResponseEntity<String> response = api.getApproaches("Tom", request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    
    Map<String, BigDecimal> map = new Gson().fromJson(response.getBody(), new TypeToken<Map<String, BigDecimal>>(){}.getType());
    assertNotNull(map);
    assertFalse(map.isEmpty());
    assertEquals(mapMock, map);
    
  }

}