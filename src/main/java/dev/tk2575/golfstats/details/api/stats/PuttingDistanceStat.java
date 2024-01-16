package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class PuttingDistanceStat implements StatsApiValueSupplier {
  private final Integer distance;
  private final BigDecimal makePercentage;
  private final BigDecimal threePuttPercentage;
  private final BigDecimal averagePutts;
  private final Integer count;
  
  public PuttingDistanceStat(@NonNull List<Shot> shots) {
    this.distance = shots.get(0).getDistanceFromTarget().getValue().intValue();
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
  }

  public static List<String> headers() {
    return List.of("Distance", "Make %", "3-Putt %", "Avg Putts", "Count");
  }

  public List<String> values() {
    return List.of(
        distance.toString(),
        makePercentage.toString(),
        threePuttPercentage.toString(),
        averagePutts.toString(),
        count.toString());
  }
}
