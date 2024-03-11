package dev.tk2575.golfstats.core.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class RollingStat implements StatsApiValueSupplier {
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
    
    public RollingStat(String name, Map.Entry<BigDecimal, Long> entry, GolfRound round) {
        this(name, entry.getValue(), round.getDate(), entry.getKey());
    }
}
