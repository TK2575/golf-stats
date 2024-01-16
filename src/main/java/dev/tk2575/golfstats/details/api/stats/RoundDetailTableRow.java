package dev.tk2575.golfstats.details.api.stats;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoundDetailTableRow implements StatsApiValueSupplier {
  private final List<String> values;
  @Override
  public List<String> values() {
    return this.values;
  }
}
