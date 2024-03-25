package dev.tk2575.golfstats.core.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.function.ToLongFunction;

@RequiredArgsConstructor
@Getter
public class GolfRoundRollingStat implements StatsApiValueSupplier {
  private final String name;
  private final Long sequence;
  private final LocalDate date;
  private final BigDecimal value;

  public List<String> values() {
    return List.of(name, sequence.toString(), date.toString(), value.toString());
  }

  public static List<String> headers() {
    return List.of("Name", "Sequence", "Date", "Value");
  }

  public GolfRoundRollingStat(String name, Map.Entry<BigDecimal, Long> entry, GolfRound round) {
    this(name, entry.getValue(), round.getDate(), entry.getKey());
  }

  public static List<GolfRoundRollingStat> generate(
      GolfRoundStream rounds, 
      String statName, 
      ToLongFunction<GolfRound> statFunction, 
      int window, 
      Optional<LongPredicate> filter) {
    var movingAverage = new MovingAverage(window);
    List<GolfRoundRollingStat> results = new ArrayList<>();
    rounds.forEachOrdered(round -> {
      long val = statFunction.applyAsLong(round);
      if (filter.isEmpty() || filter.get().test(val)) {
        results.add(new GolfRoundRollingStat(statName, movingAverage.next(new BigDecimal(val)), round));
      }
    });
    return results;
  }
}
