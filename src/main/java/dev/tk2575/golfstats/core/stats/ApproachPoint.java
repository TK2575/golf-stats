package dev.tk2575.golfstats.core.stats;

import dev.tk2575.MovingAverage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ApproachPoint {
    private final LocalDate date;
    private final ApproachBin bin;
    private final BigDecimal strokesGained;
    private final MovingAverage movingAverage;
}
