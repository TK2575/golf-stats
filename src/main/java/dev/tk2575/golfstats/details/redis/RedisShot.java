package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Lie;
import dev.tk2575.golfstats.core.golfround.shotbyshot.MissAngle;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;

import java.util.List;

class RedisShot {

  private String lie; //abbrev
  private String resultLie; //abbrev
  private int sequence;
  private RedisDistance distanceFromTarget;
  private RedisDistance missDistance;
  private String missAngleAbbreviation;
  private int count;
  
  RedisShot(Shot shot) {
    this.lie = shot.getLie().getAbbrev();
    this.resultLie = shot.getResultLie().getAbbrev();
    this.sequence = shot.getSequence();
    this.distanceFromTarget = new RedisDistance(shot.getDistanceFromTarget());
    this.missDistance = new RedisDistance(shot.getMissDistance());
    this.missAngleAbbreviation = shot.getMissAngle().getAbbreviation();
    this.count = shot.getCount();
  }

  Shot toShot() {
    return Shot.of(
        Lie.parse(this.lie.charAt(0)),
        this.sequence,
        this.distanceFromTarget.toDistance(),
        MissAngle.parse(this.missAngleAbbreviation),
        this.missDistance.toDistance(),
        Lie.parse(this.resultLie.charAt(0)),
        this.count
    );

  }

  static List<RedisShot> compile(ShotStream shots) {
    return shots.map(RedisShot::new).toList();
  }
}
