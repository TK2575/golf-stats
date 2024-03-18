package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;

import java.util.List;

class RedisShot {
  
  private String lie; //label
  private String resultLie; //label
  private int sequence;
  private RedisDistance distanceFromTarget;
  private RedisDistance missDistance;
  private String missAngleAbbreviation;
  private int count;
  
  
  RedisShot(Shot shot) {
    this.lie = shot.getLie().getLabel();
    this.resultLie = shot.getResultLie().getLabel();
    this.sequence = shot.getSequence();
    this.distanceFromTarget = new RedisDistance(shot.getDistanceFromTarget());
    this.missDistance = new RedisDistance(shot.getMissDistance());
    this.missAngleAbbreviation = shot.getMissAngle().getAbbreviation();
    this.count = shot.getCount();
  }
  
  static List<RedisShot> compile(ShotStream shots) {
    return shots.map(RedisShot::new).toList();
  }
}
