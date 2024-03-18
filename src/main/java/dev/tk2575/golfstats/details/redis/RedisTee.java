package dev.tk2575.golfstats.details.redis;

import dev.tk2575.golfstats.core.course.tee.Tee;

import java.math.BigDecimal;

class RedisTee {
  
  private String name;
  private BigDecimal rating;
  private BigDecimal slope;
  private int par;
  private int holeCount;
  
  
  RedisTee(Tee tee) {
    this.name = tee.getName();
    this.rating = tee.getRating();
    this.slope = tee.getSlope();
    this.par = tee.getPar();
    this.holeCount = tee.getHoleCount();
  }
}
