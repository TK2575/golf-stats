package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Distance;

class RedisDistance {
  
  private long value;
  private String lengthUnit;
  
  RedisDistance(Distance distance) {
    this.value = distance.getValue();
    this.lengthUnit = distance.getLengthUnit();
  }
  
  Distance toDistance() {
    return Distance.of(this.value, this.lengthUnit);
  }
}
