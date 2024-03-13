package dev.tk2575.golfstats.core.stats;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleStat implements StatsApiValueSupplier {
  private final String label;
  private final BigDecimal value;
  
  static List<SimpleStat> compile(@NonNull Map<ApproachCategory.Bin,BigDecimal> map) {
    return map.entrySet().stream()
              .map(entry -> new SimpleStat(entry.getKey().getLabel(), entry.getValue()))
              .toList();
  }
  
  @Override
  public List<String> values() {
    return List.of(label, value.toPlainString());
  }
}
