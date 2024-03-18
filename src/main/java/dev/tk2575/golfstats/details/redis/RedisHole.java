package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;

import java.util.ArrayList;
import java.util.List;

class RedisHole {
  
  private int number;
  private int index;
  private int par;
  private int strokes;
  private int strokesAdjusted;
  private int netStrokes;
  private int putts;
  private boolean fairwayInRegulation;
  private List<RedisShot> shots;
  
  RedisHole(Hole hole) {
    this.number = hole.getNumber();
    this.index = hole.getIndex();
    this.par = hole.getPar();
    this.strokes = hole.getStrokes();
    this.strokesAdjusted = hole.getStrokesAdjusted();
    this.netStrokes = hole.getNetStrokes();
    this.putts = hole.getPutts();
    this.fairwayInRegulation = hole.isFairwayInRegulation();
    this.shots = RedisShot.compile(hole.getShots());
  }
  
  static List<RedisHole> compile(HoleStream holes) {
    return holes.map(RedisHole::new).toList();
  }
}
