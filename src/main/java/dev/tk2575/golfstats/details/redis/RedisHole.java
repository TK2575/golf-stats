package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;

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
  
  Hole toHole() {
    List<Shot> shotList = shots.stream().map(RedisShot::toShot).toList();
    if (shotList.isEmpty()) {
      return Hole.of(this.number, this.index, this.par, this.strokes, this.fairwayInRegulation, this.putts);
    }
    return Hole.of(this.number, this.index, this.par, shotList);
  }
  
  static List<RedisHole> compile(HoleStream holes) {
    return holes.map(RedisHole::new).toList();
  }
}
