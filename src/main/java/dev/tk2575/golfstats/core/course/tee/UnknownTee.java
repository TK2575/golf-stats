package dev.tk2575.golfstats.core.course.tee;

import java.math.BigDecimal;

public class UnknownTee implements Tee {
  @Override
  public String getName() {
    return "UNKNOWN";
  }

  @Override
  public BigDecimal getRating() {
    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal getSlope() {
    return BigDecimal.ZERO;
  }

  @Override
  public Integer getPar() {
    return 0;
  }

  @Override
  public Integer getHoleCount() {
    return 0;
  }
}
