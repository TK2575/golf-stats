package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.RoundMeta;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class RedisGolfRound implements Serializable {

  public static final String KEY = "rounds";

  @Serial
  private static final long serialVersionUID = 1L;

  private static final String DATE_FORMAT = "yyyy-MM-dd";

  private String roundId;
  private String date;
  private long durationMinutes;
  private List<RedisHole> holes;
  private RedisGolfer golfer;
  private RedisCourse course;
  private RedisTee tee;
  private String transport;
  private BigDecimal incomingHandicapIndex;
  private BigDecimal scoreDifferential;
  private int strokes;
  private int strokesAdjusted;
  private int fairwaysInRegulation;
  private int fairways;
  private int greensInRegulation;
  private int putts;
  private int holeCount;
  private boolean nineHoleRound;
  private int netStrokes;

  RedisGolfRound(GolfRound round, String roundId) {
    this.roundId = roundId;
    this.date = round.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    this.durationMinutes = round.getDuration().toMinutes();
    this.holes = RedisHole.compile(round.getHoles());
    this.golfer = new RedisGolfer(round.getGolfer());
    this.course = new RedisCourse(round.getCourse());
    this.tee = new RedisTee(round.getTee());
    this.transport = round.getTransport();
    this.incomingHandicapIndex = round.getIncomingHandicapIndex();
    this.scoreDifferential = round.getScoreDifferential();
    this.strokes = round.getStrokes();
    this.strokesAdjusted = round.getStrokesAdjusted();
    this.fairwaysInRegulation = round.getFairwaysInRegulation();
    this.fairways = round.getFairways();
    this.greensInRegulation = round.getGreensInRegulation();
    this.putts = round.getPutts();
    this.holeCount = round.getHoleCount();
    this.nineHoleRound = round.isNineHoleRound();
    this.netStrokes = round.getNetStrokes();
  }

  public GolfRound toGolfRound() {
    List<Hole> holeList = this.holes.stream().map(RedisHole::toHole).toList();
    var meta = new RoundMeta(
        this.golfer.toGolfer(), 
        LocalDate.parse(this.date, DateTimeFormatter.ofPattern(DATE_FORMAT)),
        Duration.ofMinutes(this.durationMinutes),
        this.course.toCourse(), 
        this.tee.toTee(), 
        this.transport
    );
    if (holeList.isEmpty()) {
      return GolfRound.of(meta, this.strokes, this.fairwaysInRegulation, this.fairways, this.greensInRegulation, this.putts, this.nineHoleRound, this.strokesAdjusted, this.netStrokes, this.incomingHandicapIndex, this.scoreDifferential);
    }
    return GolfRound.of(meta, holeList, scoreDifferential, incomingHandicapIndex);
  }
  

}
