package dev.tk2575.golfstats.details.redis;

import com.github.fppt.jedismock.RedisServer;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class RedisServiceTest {
  
  private RedisService svc;
  
  @BeforeEach
  void setUp() throws IOException {
    var server = RedisServer.newRedisServer().start();
    this.svc = new RedisService(server.getHost(), server.getBindPort());
  }
  
  @Test
  void testSimpleSetAndGet() {
    svc.set("key", "value");
    assertEquals("value", svc.get("key"));
  }
  
  @Test
  void testSerializeAndDeserializeGolfRound() {
    LocalDate date = LocalDate.of(2023, 1, 1);
    var meta = new RoundMeta(
        Golfer.newGolfer("Golfer"),
        LocalDateTime.of(date, LocalTime.of(8, 0)),
        LocalDateTime.of(date, LocalTime.of(12, 0)),
        Course.of("Course"), 
        Tee.of("Tee", new BigDecimal("72"), new BigDecimal("113"), 72, 18)
    ); 
    var round = GolfRound.of(meta, 85, 14, 14, 18, 36, false);
    var id = svc.saveRound(round);
    var retrieved = svc.getRound(id);
    assertEquals(round, retrieved);
  }
  
  @Test
  void testGetAllRounds_returnsInvalidRounds() {
    IntStream.range(0, 10).forEach(i -> svc.set("rounds:" + i, "{}"));
    assertEquals(10, svc.getAllRounds().size());
  }
  
  @Test
  void testGetAllRounds_returnsOnlyValidRounds() {
    IntStream.range(0, 10).forEach(i -> svc.set("rounds:" + i, "{}"));
    assertEquals(0, svc.getAllRounds(true).size());
  }

}