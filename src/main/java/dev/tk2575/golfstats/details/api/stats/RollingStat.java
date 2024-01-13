package dev.tk2575.golfstats.details.api.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class RollingStat {
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
}
