package dev.tk2575.golfstats.core.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RoundDetailTableRow {
  private final List<String> values;
}
