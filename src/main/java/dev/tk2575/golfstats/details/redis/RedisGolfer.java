package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.NonNull;

class RedisGolfer {
  
  private String name;
  private String gender;
  
  
  RedisGolfer(@NonNull Golfer golfer) {
    this.name = golfer.getName();
    this.gender = golfer.getGender();
  }
  
  Golfer toGolfer() {
    return Golfer.newGolfer(this.name, this.gender);
  }
}
