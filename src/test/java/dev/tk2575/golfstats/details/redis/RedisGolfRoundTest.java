package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RedisGolfRoundTest {
  
  @Test
  void testConversion() {
    LocalDate date = LocalDate.of(2023, 1, 1);
    var meta = new RoundMeta(
        Golfer.newGolfer("Golfer"),
        LocalDateTime.of(date, LocalTime.of(8, 0)),
        LocalDateTime.of(date, LocalTime.of(12, 0)),
        Course.of("Course"), 
        Tee.of("Tee", new BigDecimal("72"), new BigDecimal("113"), 72, 18)
    ); 
    var round = GolfRound.of(meta, 85, 14, 14, 18, 36, false);
    var converted = new RedisGolfRound(round, "id");
    var returned = converted.toGolfRound();
    assertEquals(round, returned);
  }

}