package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfer.Golfer;

class RedisGolfer {
  
  private String name;
  private String gender;
  
  
  RedisGolfer(Golfer golfer) {
    this.name = golfer.getName();
    this.gender = golfer.getGender();
  }
}
