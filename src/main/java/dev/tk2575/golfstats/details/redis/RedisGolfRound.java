package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfround.GolfRound;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

class RedisGolfRound {
  
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  
  private int roundId;
  private String date;
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
  
  RedisGolfRound(GolfRound round, int roundId) {
    this.roundId = roundId;
    this.date = round.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
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
  
}
