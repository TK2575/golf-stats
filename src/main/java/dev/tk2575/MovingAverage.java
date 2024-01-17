package dev.tk2575;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

@RequiredArgsConstructor
public class MovingAverage {
  private final int windowSize;
  private final Deque<BigDecimal> queue = new ArrayDeque<>();
  private BigDecimal sum = BigDecimal.ZERO;
  private long count = 0;
  
  public Map.Entry<BigDecimal,Long> next(BigDecimal value) {
    count++;
    queue.add(value);
    BigDecimal tail = count > windowSize ? queue.poll() : BigDecimal.ZERO;
    sum = sum.add(value).subtract(tail);
    BigDecimal avg = sum.divide(BigDecimal.valueOf(Math.min(count, windowSize)), 4, RoundingMode.HALF_UP);
    return Map.entry(avg, count);
  }
}
