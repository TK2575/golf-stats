package dev.tk2575.golfstats.core.stats;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public class PuttingDistanceStat implements StatsApiValueSupplier {
  private final Integer distance;
  private final BigDecimal makePercentage;
  private final BigDecimal threePuttPercentage;
  private final BigDecimal averagePutts;
  private final Integer count;

  private final BigDecimal strokesGained;

  private final BigDecimal avgStrokesGained;
  
  public PuttingDistanceStat(@NonNull List<Shot> shots, Long bin) {
    this.distance = bin.intValue();
    this.makePercentage = Utils.divide(
        shots.stream().filter(shot -> shot.getCount() == 1).count(), 
        shots.size(), 
        4
    );
    this.threePuttPercentage = Utils.divide(
        shots.stream().filter(shot -> shot.getCount() > 2).count(), 
        shots.size(), 
        4
    );
    this.averagePutts = Utils.divide(
        shots.stream().map(Shot::getCount).reduce(0, Integer::sum),
        shots.size(),
        4
    );
    this.count = shots.size();
    this.strokesGained = shots.stream().map(Shot::getStrokesGained).reduce(BigDecimal.ZERO, BigDecimal::add);
    this.avgStrokesGained = Utils.divide(
            shots.stream().map(Shot::getStrokesGained).reduce(BigDecimal.ZERO, BigDecimal::add),
            this.count);
  }
  
  public PuttingDistanceStat(@NonNull List<Shot> shots) {
    this(shots, shots.get(0).getDistanceFromTarget().getValue());
  }

  public static List<String> headers() {
    return List.of("Distance", "Make %", "3-Putt %", "Avg Putts", "Count", "Strokes Gained", "Avg SG");
  }
  
  public static Function<Shot, Long> distance() {
    return shot -> shot.getDistanceFromTarget().getLengthInFeet();
  }

  public static Function<Shot, Long> binDistance() {
    return shot -> {
      var dist = shot.getDistanceFromTarget().getLengthInFeet();
      if (dist <= 10) return dist;
      if (dist <= 15) return 15L;
      if (dist <= 20) return 20L;
      if (dist <= 30) return 30L;
      return 40L;
    };
  }

  public List<String> values() {
    return List.of(
        distance.toString(),
        makePercentage.toString(),
        threePuttPercentage.toString(),
        averagePutts.toString(),
        count.toString(),
        strokesGained.toPlainString(),
        avgStrokesGained.toPlainString());
  }
}
